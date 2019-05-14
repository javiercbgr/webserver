package me.homework.server;

import me.homework.server.apps.WebApp;
import me.homework.server.helpers.Logger;
import me.homework.server.helpers.SocketConfig;
import me.homework.server.workers.ExecutorMonitor;
import me.homework.server.workers.Handler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.net.StandardSocketOptions;
import java.net.ServerSocket;
import java.net.InetSocketAddress;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Future;

/**
 * Request dispatcher and the main entry point of the application.
 * Listens on a port for new connections and accepts them. If keep-alive is 
 * detected (in HTTP/1.1 is the default mode), it keeps the connection open.
 * 
 * New connections and keep-alive connections are handled by an executor pool of
 * threads.
 *
 * Created by Mihail on 10/24/2015.
 * Modified by Javier on 14/05/2019.
 */
public class WebServer implements Runnable {

    public final static String TAG = "me.homework.server.WebServer";
    public final static String SERVER_NAME = "WebServer/0.1";

    private final int CLEAN_UP_INTERVAL = 5000;

    private final int WORKER_POOL_THREAD_TIMEOUT = 8;

    private final int port;

    /** Amount of thread in the executor pool. */
    private final int workers;

    /** The web app that will be used to handle the requests. */
    private final WebApp app;

    /** Socket server channel for accepting new connections. */
    private ServerSocketChannel serverChannel;

    private LinkedBlockingQueue<Runnable> connections;
    private ExecutorCompletionService<SocketChannel> keepAliveChannels;
    private ThreadPoolExecutor pool;
    private Thread monitor;

    public WebServer(int port, int workers, WebApp app) {
        this.port = port;
        this.workers = workers;
        this.app = app;
    }

    /**
     * Check the pool for completion of one or more of its task. In case it's a
     * keep-alive register the connection with the read selector for upcoming
     * messages.
     * 
     * @param selector The selector where to register keep alive channels.
     */
    private void registerKeepAliveChannels(Selector selector) 
                                                throws IOException, 
                                                       InterruptedException {
        long now = System.currentTimeMillis();
        Future<SocketChannel> alive;
        while ((alive = keepAliveChannels.poll()) != null) {
            try {
                SocketChannel channel = alive.get();
                if (channel != null) {
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ, now);
                    Logger.info("Adding channel back again!");
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Each x seconds check all registrations if they have timed-out in which 
     * case the corresponding channel is closed.
     * 
     * @param lastCleanup Last time a clean up was performed.
     * @param keys The set of SelectionKey to check.
     * 
     * @return The current time if a clean up is performed. If not, the last 
     *     clean up time.
     */
    private long cleanupKeepAliveChannels(long lastCleanup, 
                                          Set<SelectionKey> keys) 
                                                throws IOException {
        long now = System.currentTimeMillis();
        if (now - lastCleanup > CLEAN_UP_INTERVAL) {
            for (SelectionKey key : keys) {
                if ((key.interestOps() & SelectionKey.OP_READ) != 0 &&
                    ((Long)key.attachment()) 
                        + SocketConfig.keepAliveTime < now) {
                    Logger.info("Closing connection to " + key.channel());
                    key.channel().close();
                }
            }
            return now;
        }
        return lastCleanup;
    }

    @Override
    public void run() {
        Selector selector = null;
        try {
            serverChannel = ServerSocketChannel.open(); 
            serverChannel.configureBlocking(false);

            ServerSocket serverSocket = serverChannel.socket();
            serverSocket.bind(new InetSocketAddress(port));

            selector = Selector.open();
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

            connections = new LinkedBlockingQueue<>();
            pool = new ThreadPoolExecutor(workers, 
                                          workers, 
                                          WORKER_POOL_THREAD_TIMEOUT, 
                                          TimeUnit.SECONDS, 
                                          connections);
            keepAliveChannels 
                = new ExecutorCompletionService<SocketChannel>(pool);

            monitor = new Thread(new ExecutorMonitor(pool));
        } catch (IOException e) {
            Logger.error(e.getMessage());
            System.exit(1);
        }

        monitor.setDaemon(true);
        monitor.start();

        long lastCleanup = System.currentTimeMillis();
        while (true) {
            try {
                this.registerKeepAliveChannels(selector);
                lastCleanup = this.cleanupKeepAliveChannels(lastCleanup, 
                                                            selector.keys());

                selector.selectNow();
                for (SelectionKey key : selector.selectedKeys()) {
                    if (key.isAcceptable()) {
                        SocketChannel clientChannel = serverChannel.accept();
                        if (clientChannel != null) {
                            clientChannel.configureBlocking(true);
                            keepAliveChannels.submit(new Handler(clientChannel, 
                                                                 app));
                            Logger.info("New SocketChannel created.");
                        }
                    } else if (key.isReadable()) {
                        SocketChannel clientChannel 
                            = (SocketChannel) key.channel();
                        key.cancel();
                        Logger.info("Reusing channel...");
                        clientChannel.configureBlocking(true);
                        keepAliveChannels.submit(new Handler(clientChannel, 
                            app));
                    }
                }
          
                selector.selectNow();  
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                Logger.error(e.getMessage());
            }
        }
    }
}
