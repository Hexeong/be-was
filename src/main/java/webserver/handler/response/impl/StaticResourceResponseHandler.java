package webserver.handler.response.impl;

import extractor.http.FileTypeExtractor;
import model.http.TotalHttpMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.handler.response.ResponseHandler;
import writer.ResponseWriterFacade;
import writer.file.StaticResourceType;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class StaticResourceResponseHandler implements ResponseHandler {
    private static final String BASE_DIRECTORY_PATH = "./src/main/resources/static";

    private static final Logger log = LoggerFactory.getLogger(StaticResourceResponseHandler.class);

    public StaticResourceResponseHandler() {}

    public boolean sendResponse(OutputStream out, TotalHttpMessage msg) {
        String fileExtension = FileTypeExtractor.getInstance().extract(msg.line().getPathUrl());

        StaticResourceType staticResourceType = StaticResourceType.findByType(fileExtension);

        File file = new File(BASE_DIRECTORY_PATH + msg.line().getPathUrl());

        try {
            byte[] body = Files.readAllBytes(file.toPath());

            ResponseWriterFacade.send200FileResponse(out, staticResourceType.getContentType(), body);
            return true;
        } catch (IOException e) {
            log.error(e.getMessage());
            ResponseWriterFacade.send404NotFoundResponse(out);
            return false;
        }
    }
}
