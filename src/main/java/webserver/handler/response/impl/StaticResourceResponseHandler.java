package webserver.handler.response.impl;

import extractor.FileTypeExtractor;
import model.http.TotalHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handler.response.ResponseHandler;
import writer.ResponseBodyWriter;
import writer.ResponseHeaderWriter;
import writer.file.StaticResourceType;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticResourceResponseHandler implements ResponseHandler {
    private static final String BASE_DIRECTORY_PATH = "./src/main/resources/static";

    private static final Logger log = LoggerFactory.getLogger(StaticResourceResponseHandler.class);

    public StaticResourceResponseHandler() {}

    public void sendResponse(OutputStream out, TotalHttpMessage message) {
        String fileExtension = FileTypeExtractor.getInstance().extractFileExtensionFromURL(message.line().url());

        StaticResourceType staticResourceType = StaticResourceType.findByType(fileExtension);

        File file = new File(BASE_DIRECTORY_PATH + message.line().url());

        byte[] body;
        try {
            body = Files.readAllBytes(file.toPath());

            DataOutputStream dos = new DataOutputStream(out);
            ResponseHeaderWriter.getInstance().write200Header(dos, staticResourceType, body.length);
            ResponseBodyWriter.getInstance().writeBody(dos, body);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
