package com.j2kb.goal.exception;

public class DuplicateMemberException extends RuntimeException{
    public DuplicateMemberException(String msg){
        super(msg);
    }

    public DuplicateMemberException(String msg,Throwable throwable){
        super(msg);
        this.initCause(throwable);
    }
}
