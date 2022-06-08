package com.j2kb.goal.exception;

import org.springframework.http.HttpStatus;

public class NoMatchedAdminException extends SpringHandledException{
    public NoMatchedAdminException(HttpStatus httpStatus, int errorCode, String url, String msg) {
        super(httpStatus, errorCode, url, msg);
    }
}
