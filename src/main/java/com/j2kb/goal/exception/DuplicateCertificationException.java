package com.j2kb.goal.exception;

public class DuplicateCertificationException extends RuntimeException{
    public DuplicateCertificationException(String msg){
        super(msg);
    }

    public DuplicateCertificationException(String msg,Throwable throwable){
        super(msg);
        this.initCause(throwable);
    }
}
