package cz.serachtool.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice()
public class HttpMessageNotReadableExceptionAdvice {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleException(HttpMessageNotReadableException e ){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Query must not be null"));
    }
}
