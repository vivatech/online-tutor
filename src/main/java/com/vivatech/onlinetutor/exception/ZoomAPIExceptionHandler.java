package com.vivatech.onlinetutor.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;

public class ZoomAPIExceptionHandler extends RuntimeException {

    @Getter
    private final int code;
    private final String message;

    public ZoomAPIExceptionHandler(String jsonBody) {
        int code = 0;
        String message = "";
        try {
            var json = new ObjectMapper().readTree(jsonBody);
            System.out.println("Received jsonBody " + jsonBody);
            code = json.path("code").asInt();
            message = json.path("message").asText();
            System.out.println("Parsed Json response: " + code + " " + message);
        } catch (Exception e) {
            throw new OnlineTutorExceptionHandler("Conversion Failed.");
        }
        this.code = code;
        this.message = message;
    }

    @Override public String getMessage() { return message; }
}
