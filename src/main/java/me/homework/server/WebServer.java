package me.homework.server;

import me.homework.server.apps.WebApp;
import me.homework.server.helpers.Logger;
import me.homework.server.workers.ExecutorMonitor;
import me.homework.server.workers.Handler;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Request dispatcher and the main entry point of the application.
 * Listens on a port for new connections and accepts them. New connections are handled by the executor pool.
 */
public class WebServer implements Runnable {

    public final static String TAG = "me.homework.server.WebServer";
    public final static String SERVER_NAME = "WebServer/0.1";

    private final int port;
    private final int workers;
    private final WebApp app;

    private ServerSocket server;
    private LinkedBlockingQueue<Runnable> connections;
    private ThreadPoolExecutor pool;
    private Thread monitor;

    public WebServer(int port, int workers, WebApp app) {
        this.port = port;
        this.workers = workers;
        this.app = app;
    }

    public void run() {
        try {
            server = new ServerSocket(port);
            connections = new LinkedBlockingQueue<>();
            pool = new ThreadPoolExecutor(workers, workers, 10, TimeUnit.SECONDS, connections);

            monitor = new Thread(new ExecutorMonitor(pool));
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }

        monitor.setDaemon(true);
        monitor.start();

        while (true) {
            try {
                pool.execute(new Handler(server.accept(), app));
            } catch (IOException e) {
                Logger.error(e.getMessage());
            }
        }
    }

}
