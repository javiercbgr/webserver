package me.homework.server.workers;

import me.homework.server.WebServer;
import me.homework.server.apps.WebApp;
import me.homework.server.exceptions.BadRequestException;
import me.homework.server.exceptions.ConnectionClosedException;
import me.homework.server.helpers.Logger;
import me.homework.server.helpers.PerformanceStats;
import me.homework.server.http.EmptyHttpResponse;
import me.homework.server.http.HttpRequest;
import me.homework.server.http.HttpResponse;
import me.homework.server.http.RawHttpRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Calendar;

/**
 * Created by Mihail on 10/24/2015.
 */
public class Handler implements Runnable {

    private WebApp app;
    private Socket connection;
    private InputStream in;
    private OutputStream out;

    public Handler(Socket socket, WebApp app) {
        this.connection = socket;
        this.app = app;
    }

    @Override
    public void run() {
        try {
            in = connection.getInputStream();
            out = connection.getOutputStream();

            HttpRequest request = HttpRequest.parse(in);

            if (request != null) {
                Logger.info(request.getRequestLine() +
                        " from "
                        + connection.getInetAddress()
                        + ":"
                        + connection.getPort());

                HttpResponse response = app.handle(request);

                response.getHeaders().put("Server", WebServer.SERVER_NAME);
                response.getHeaders().put("Date", Calendar.getInstance().getTime().toString());
                response.getHeaders().put("Connection", "close"); // TODO Add keep-alive connections?

                response.write(out);
            } else {
                Logger.error("Server accepts only HTTP protocol.");
                new RawHttpRequest(501, "Server only accepts HTTP protocol").write(out);
            }
        } catch (BadRequestException e) {
            Logger.error("Bad Request");
            new RawHttpRequest(400, "Server only accepts HTTP protocol").write(out);
        } catch (IOException e) {
            Logger.error("Error in client's IO.");
        }  catch (ConnectionClosedException e) {
            Logger.error("Connection closed by client");
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                Logger.error("Error while closing input stream.");
            }
            try {
                out.close();
            } catch (IOException e) {
                Logger.error("Error while closing output stream.");
            }
            try {
                connection.close();
            } catch (IOException e) {
                Logger.error("Error while closing client socket.");
            }
        }
    }

}
