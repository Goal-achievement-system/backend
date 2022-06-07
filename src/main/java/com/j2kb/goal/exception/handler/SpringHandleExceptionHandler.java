package com.j2kb.goal.exception.handler;

import com.j2kb.goal.exception.SpringHandledException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class SpringHandleExceptionHandler {

    @ExceptionHandler(SpringHandledException.class)
    public ResponseEntity<?> handle(SpringHandledException exception){
        exception.printStackTrace();
        return exception.parseResponseEntity();
    }
}
