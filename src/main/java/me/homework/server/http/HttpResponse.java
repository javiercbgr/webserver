package me.homework.server.http;

import java.io.*;
import java.util.HashMap;

/**
 * HTTP Response abstract class used in communication with the client.
 * This class contains the data that will be sent to the client,
 * including status line, headers, and response body
 * 
 * Created by Mihail on 10/24/2015.
 * Formatted by Javier on 14/05/2019.
 */
public abstract class HttpResponse {
    /**
     * The {@link String} that represents the protocol version, used in the 
     * status line (e. g. HTTP/1.1).
     */
    protected String protocol = "HTTP/1.1";

    /**
     * The status code element is a 3-digit {@link int} result code of the 
     * attempt to understand and satisfy the
     * request. There are 5 classes of responses:
     * 1xx: Informational - Request received, continuing process.
     * 2xx: Success - The action was successfully received, understood, and 
     *     accepted.
     * 3xx: Redirection - Further action must be taken in order to complete the 
     *     request.
     * 4xx: Client Error - The request contains bad syntax or cannot be 
     *     fulfilled.
     * 5xx: Server Error - The server failed to fulfill an apparently valid 
     *     request.
     * @see HttpStatus for available status codes
     */
    protected int statusCode;

    /**
     * The {@link HashMap} that contains the headers sent along with the 
     * response.
     * 
     * The headers allow the server to pass additional information about the 
     * response which cannot be placed in the status line. These header 
     * fields give information about the server and about further access to 
     * the resource identified by the request URI.
     */
    protected HashMap<String, String> headers;

    public HttpResponse() {
        this.headers = new HashMap<>();
    }

    /**
     * This function writes the HTTP response to an output stream.
     *
     * @param out the target {@link OutputStream} for writing
     */
    abstract public void write(OutputStream out);

    /**
     * @return the response line containing protocol version and response 
     *     status.
     */
    public String getResponseLine() {
        return protocol
                + " "
                + String.valueOf(statusCode)
                + " "
                + HttpStatus.reasons.getOrDefault(statusCode, "Not Specified");
    }

    /**
     * @return {@link String} that represents the protocol version, used in the 
     *     status line (e. g. HTTP/1.1).
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * @param protocol The protocol to be sent in the status line.
     */
    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @param statusCode The status code to be sent in the status line.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    /**
     * @param headers The headers to be sent to the client
     */
    public void setHeaders(HashMap<String, String> headers) {
        this.headers = headers;
    }
}

