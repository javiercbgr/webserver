package me.homework.server.workers;

import me.homework.server.WebServer;
import me.homework.server.apps.WebApp;
import me.homework.server.exceptions.BadRequestException;
import me.homework.server.exceptions.ConnectionClosedException;
import me.homework.server.helpers.Logger;
import me.homework.server.helpers.PerformanceStats;
import me.homework.server.helpers.SocketConfig;
import me.homework.server.http.EmptyHttpResponse;
import me.homework.server.http.HttpRequest;
import me.homework.server.http.HttpResponse;
import me.homework.server.http.RawHttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.Callable;
import java.nio.channels.SocketChannel;

/**
 * Handles a request, deciding based on its header wether to keep-alive
 * the socket connection.
 * 
 * Created by Mihail on 10/24/2015.
 * Modified by Javier on 12/05/2019.
 */
public class Handler implements Callable<SocketChannel> {

    private WebApp app;
    private SocketChannel socketChannel;
    private InputStream in;
    private OutputStream out;

    /** Used to measure the request handling time. */
    private long creationTime;

    public Handler(SocketChannel socketChannel, WebApp app) {
        this.socketChannel = socketChannel;
        this.app = app;
        this.creationTime = System.currentTimeMillis();
    }

    @Override
    public SocketChannel call() {
        Logger.info("*** Thread processing started! ***");
        HttpRequest request = null;
        Socket socket = null;
        try {

            socket = this.socketChannel.socket();
            socket.setSoTimeout(SocketConfig.socketTimeout);

            in = socket.getInputStream();
            out = socket.getOutputStream();
            Logger.info("Parsing request...");
            request = HttpRequest.parse(in);

            if (request != null) {
                Logger.info(request.getRequestLine() + " from " 
                    + socketChannel.getRemoteAddress());

                HttpResponse response = app.handle(request);

                // Build response headers.
                response.getHeaders().put("Server", WebServer.SERVER_NAME);
                response.getHeaders().put("Date", 
                    Calendar.getInstance().getTime().toString());
                if (request.isKeepAlive()) {
                    response.getHeaders().put("Connection", "keep-alive"); 
                    response.getHeaders().put("Keep-Alive", "timeout=" 
                        + (SocketConfig.keepAliveTime / 1000)); 
                } else {
                    response.getHeaders().put("Connection", "close"); 
                }

                response.write(out);

                int handlingTime = (int) (System.currentTimeMillis() 
                    - this.creationTime);
                PerformanceStats.recordHandlingTime(handlingTime);
                Logger.info("Request handled successfully!");
            } else {
                new RawHttpRequest(501, "Server only accepts HTTP protocol")
                    .write(out);
            }
        } catch (BadRequestException e) {
            Logger.info("Bad request.");
            new RawHttpRequest(400, "Server only accepts HTTP protocol.")
                .write(out);
        } catch (SocketTimeoutException e) {
            Logger.info("Timeout while keeping thread aliving.");
        } catch (IOException e) {
            Logger.info("Error in client's IO.");
        }  catch (ConnectionClosedException e) {
            Logger.info("Connection closed by client.");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if (request == null || !request.isKeepAlive()) {
                try {
                    in.close();
                } catch (IOException e) {
                    Logger.info("Error while closing input stream.");
                }
                try {
                    out.close();
                } catch (IOException e) {
                    Logger.info("Error while closing output stream.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    Logger.info("Error while closing socket.");
                }
                return null;
            } else {
                // Return for socket reuse.
                return this.socketChannel;
            }
        }
    }

}
