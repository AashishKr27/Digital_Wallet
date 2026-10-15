package com.example.digitalwallet.exception;

public class SelfTransferNotAllowedException extends  RuntimeException{
    public SelfTransferNotAllowedException(){
        super("Self transfer not allowed");
    }
}
