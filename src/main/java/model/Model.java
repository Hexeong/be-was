package model;

import exception.CustomException;

import java.util.LinkedHashMap;

public class Model extends LinkedHashMap<String, Object> {

    public Model() {
    }

    public Model(CustomException e) {
        this.put("statusCode", e.getCode().getStatus().getCode() + " " + e.getCode().getStatus().getMessage());

        String fullMessage = e.getCode().getMessage();

        if (e.getSpecificMessage() != null && !e.getSpecificMessage().isEmpty()) {
            fullMessage += "\n(상세: " + e.getSpecificMessage() + ")";
        }

        this.put("statusMessage", fullMessage);
    }
}
