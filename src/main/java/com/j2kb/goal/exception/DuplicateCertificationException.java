package com.j2kb.goal.exception;

import org.springframework.http.HttpStatus;

public class DuplicateCertificationException extends SpringHandledException{
    public DuplicateCertificationException(HttpStatus httpStatus, int errorCode, String url, String msg) {
        super(httpStatus, errorCode, url, msg);
    }
}
