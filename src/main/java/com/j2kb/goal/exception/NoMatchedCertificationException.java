package com.j2kb.goal.exception;

public class NoMatchedCertificationException extends RuntimeException{
    public NoMatchedCertificationException(String msg){
        super(msg);
    }

    public NoMatchedCertificationException(String msg, Throwable throwable){
        super(msg);
        this.initCause(throwable);
    }
}
