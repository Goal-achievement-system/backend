package com.j2kb.goal.exception;

import org.springframework.http.HttpStatus;

public class NoMatchedMemberException extends SpringHandledException{

    public NoMatchedMemberException(HttpStatus httpStatus, int errorCode, String url, String msg) {
        super(httpStatus, errorCode, url, msg);
    }
}
