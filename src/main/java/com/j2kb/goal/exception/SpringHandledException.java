package com.j2kb.goal.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(pattern = "yyyy-MM-dd kk:mm:ss")
        private LocalDateTime dateTime;
    }
    public final ResponseEntity<Error> parseResponseEntity(){
        return ResponseEntity.status(httpStatus).body(new Error(errorCode,msg,url,LocalDateTime.now()));
    }
}
