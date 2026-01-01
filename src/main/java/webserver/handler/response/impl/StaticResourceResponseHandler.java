package webserver.handler.response.impl;

import extractor.http.FileTypeExtractor;
import model.http.HttpStatus;
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
import java.util.Map;

public class StaticResourceResponseHandler implements ResponseHandler {
    private static final String BASE_DIRECTORY_PATH = "./src/main/resources/static";

    private static final Logger log = LoggerFactory.getLogger(StaticResourceResponseHandler.class);

    public StaticResourceResponseHandler() {}

    public boolean sendResponse(OutputStream out, TotalHttpMessage message) {
        DataOutputStream dos = new DataOutputStream(out);

        String fileExtension = FileTypeExtractor.getInstance().extract(message.line().getPathUrl());

        StaticResourceType staticResourceType = StaticResourceType.findByType(fileExtension);

        File file = new File(BASE_DIRECTORY_PATH + message.line().getPathUrl());

        byte[] body;
        try {
            body = Files.readAllBytes(file.toPath());

            ResponseHeaderWriter.writeHeader(
                    dos,
                    Map.of("Content-Type", staticResourceType.getContentType()),
                    body.length,
                    HttpStatus.OK);
            ResponseBodyWriter.writeBody(dos, body);
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
            ResponseHeaderWriter.writeHeader(
                    dos,
                    Map.of("Content-Type", StaticResourceType.HTML.getContentType()),
                    0,
                    HttpStatus.NOT_FOUND);
            ResponseBodyWriter.writeBody(dos, new byte[0]);
            return false;
        }
    }
}
