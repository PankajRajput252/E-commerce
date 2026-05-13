package com.gunwala.exceptions;

public class FinalException extends RuntimeException {

    String msg;
    public FinalException(){};

    public FinalException(String msg){
        super(msg);
        this.msg=msg;
    }

}
