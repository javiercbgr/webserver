package me.homework.server.http;

import me.homework.server.helpers.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

/**
 * HttpResponse extensions that sends a file to the client (e. g. html)
 */
public class FileHttpResponse extends HttpResponse {

    /**
     * File to be sent to the user.
     */
    private File inputFile;

    public FileHttpResponse(int statusCode, File inputFile) {
        super();

        this.statusCode = statusCode;
        this.inputFile = inputFile;

        try {
            this.setContentType();
            this.setContentLength();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    /**
     * This function writes the HTTP response to an output stream.
     *
     * @param out the target {@link OutputStream} for writing
     */
    public void write(OutputStream out) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(getResponseLine());
            writer.write("\r\n");

            for (String key: headers.keySet()) {
                writer.write(key + ":" + headers.get(key));
                writer.write("\r\n");
            }
            writer.write("\r\n");

            if (inputFile != null) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
                char[] buffer = new char[1024];
                int read;
                while ((read = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, read);
                }
                reader.close();
            }

            writer.flush();
        } catch (IOException e) {
            Logger.error(e.getMessage());
        }
    }

    private void setContentType() throws IOException {
        Path source = Paths.get(this.inputFile.toURI());
        String contentType = Files.probeContentType(source);
        if (contentType != null) {
            headers.put("Content-Type", contentType);
        }
    }

    private void setContentLength() throws IOException {
        headers.put("Content-Length", String.valueOf(this.inputFile.length()));
    }
}

