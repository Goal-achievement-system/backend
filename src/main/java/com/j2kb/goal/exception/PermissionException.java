package com.j2kb.goal.exception;

import org.springframework.http.HttpStatus;

public class PermissionException extends SpringHandledException{
    public PermissionException(HttpStatus httpStatus, int errorCode, String url, String msg) {
        super(httpStatus, errorCode, url, msg);
    }
}
