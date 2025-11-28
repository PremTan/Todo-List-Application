package com.mojoes.todo.exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<String> handleDuplicateEmailException(DuplicateEmailException ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<Map<String, String>> handleJwtExpireException(ExpiredJwtException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                Map.of(
                        "error", "TOKEN_EXPIRED",
                        "message", "Your token has expired. Please login again."
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAnyException(Exception ex){
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
