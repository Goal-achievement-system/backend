package com.j2kb.goal.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SpringHandledException extends RuntimeException{
    private HttpStatus httpStatus;
    private int errorCode;
    private String url;
    private String msg;

    public SpringHandledException(HttpStatus httpStatus, int errorCode, String url, String msg) {
        super(msg);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.url = url;
        this.msg = msg;
    }
    @AllArgsConstructor
    @Getter
    private class Error{
        private int errorCode;
        private String errorContent;
        private String url;
        private Timestamp timestamp;
    }
    public final ResponseEntity<Error> parseResponseEntity(){
        return ResponseEntity.status(httpStatus).body(new Error(errorCode,msg,url,Timestamp.valueOf(LocalDateTime.now())));
    }
}
