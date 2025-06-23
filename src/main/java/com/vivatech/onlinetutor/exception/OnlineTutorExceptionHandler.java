package com.vivatech.onlinetutor.exception;

public class OnlineTutorExceptionHandler extends RuntimeException {

    public OnlineTutorExceptionHandler() {
        super("Not found"); // Provide a default error message
    }

    public OnlineTutorExceptionHandler(String message) {
        super(message); // Allow custom error messages
    }
}
