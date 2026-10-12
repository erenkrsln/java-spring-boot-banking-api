
package com.example.banking_api;

import java.time.Instant;
import java.util.Map;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<Map<String, Object>> handleOptimisticLock(
            OptimisticLockingFailureException exception,
            WebRequest request) {

        return createErrorResponse(
                HttpStatus.CONFLICT,
                "Concurrent modification detected. Please retry.",
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException exception,
            WebRequest request) {

        return createErrorResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or JSON format.",
                request
        );
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(
            HttpStatus status,
            String message,
            WebRequest request) {

        Map<String, Object> error = Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message,
                "path", request.getDescription(false)
                        .replaceFirst("^uri=", "")
        );

        return ResponseEntity.status(status).body(error);
    }
}
