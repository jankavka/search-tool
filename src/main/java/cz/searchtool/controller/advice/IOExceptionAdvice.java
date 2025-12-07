package cz.searchtool.controller.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class IOExceptionAdvice {

    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handleException(IOException e ){
        return  ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Internal Server Error: An error occurred during I/O operation"));
    }
}
