package com.vivatech.onlinetutor.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.StreamCorruptedException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

@ControllerAdvice
@Slf4j
@Component
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        // Extract the first validation error message
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError != null ? fieldError.getDefaultMessage() : "Validation error occurred";

        // Log the full validation error details
        log.error("Validation failed: {}", ex.getBindingResult().toString(), ex);

        // Return the error message as a response
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(OnlineTutorExceptionHandler.class)
    public ResponseEntity<String> handleOnlineTutorException(OnlineTutorExceptionHandler ex) {
        String errorMessage = ex.getMessage();
        log.error("Exception: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
    }

    @ExceptionHandler(ZoomAPIExceptionHandler.class)
    public ResponseEntity<String> handleOnlineTutorException(ZoomAPIExceptionHandler ex) {
        log.error("Zoom API Exception: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        String errorResponse = ex.getMessage();
        log.error("Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(StreamCorruptedException.class)
    public ResponseEntity<String> handleStreamCorruptedException(StreamCorruptedException ex) {
        String errorResponse = ex.getMessage();
        log.error("Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<String> sqlIntegrityException(SQLIntegrityConstraintViolationException ex) {
        String errorResponse = ex.getMessage().split(":")[0].trim();
        log.error("Exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }


}
