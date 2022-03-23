package com.j2kb.goal.exception;

public class NoMatchedMemberException extends RuntimeException{
    public NoMatchedMemberException(String msg){
        super(msg);
    }
    public NoMatchedMemberException(String msg, Throwable throwable){
        super(msg);
        this.initCause(throwable);
    }
}
