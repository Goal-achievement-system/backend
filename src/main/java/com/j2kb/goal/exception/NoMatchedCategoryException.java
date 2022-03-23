package com.j2kb.goal.exception;

public class NoMatchedCategoryException extends RuntimeException{
    public NoMatchedCategoryException(String msg){
        super(msg);
    }

    public NoMatchedCategoryException(String msg, Throwable throwable){
        super(msg);
        this.initCause(throwable);
    }
}
