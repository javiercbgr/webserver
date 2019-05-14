package me.homework.server.apps;

import me.homework.server.http.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A static file server application.
 * 
 * Created by Mihail on 10/24/2015.
 */
public class FileServingApp implements WebApp {

    private final String documentRoot;

    public FileServingApp(String documentRoot) {
        this.documentRoot = documentRoot;
    }

    @Override
    public HttpResponse handle(HttpRequest request) {

        String path = request.getPath();

        HttpResponse response;

        switch (request.getMethod()) {
            case HttpMethod.GET:
                Path requestedFile = Paths.get(documentRoot, path);
                if (Files.exists(requestedFile)) {
                    if (Files.isDirectory(requestedFile)) {
                        response = new EmptyHttpResponse(HttpStatus.FORBIDDEN);
                    } else {
                        response = new FileHttpResponse(HttpStatus.OK,
                                new File(Paths.get(documentRoot, path)
                                    .toString()));
                    }
                } else {
                    response = new EmptyHttpResponse(HttpStatus.NOT_FOUND);
                }
                break;
            case HttpMethod.TRACE:
                response = new StreamHttpResponse(HttpStatus.OK, 
                                                  request.getInputStream());
                break;
            case HttpMethod.HEAD:
                if (Files.exists(Paths.get(documentRoot, path))) {
                    response = new HeadHttpResponse(HttpStatus.OK,
                            new File(Paths.get(documentRoot, path).toString()));
                } else {
                    response = new EmptyHttpResponse(HttpStatus.NOT_FOUND);
                }
                break;
            default:
                response = new EmptyHttpResponse(HttpStatus.NOT_IMPLEMENTED);
                break;
        }

        return response;
    }
}
