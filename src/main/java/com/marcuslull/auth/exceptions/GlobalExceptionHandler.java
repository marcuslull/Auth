package com.marcuslull.auth.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(Exception exception) {
        log.error("EXCEPTION: GlobalExceptionHandler.handleRuntimeException()", exception);
        return ResponseEntity.internalServerError().body("Internal server error, please try again later.");
    }
}
