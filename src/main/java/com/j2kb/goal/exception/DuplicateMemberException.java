package com.j2kb.goal.exception;

import org.springframework.http.HttpStatus;

public class DuplicateMemberException extends SpringHandledException{

    public DuplicateMemberException(HttpStatus httpStatus, int errorCode, String url, String msg) {
        super(httpStatus, errorCode, url, msg);
    }
}
