package com.example.digitalwallet.exception;

public class InvalidTransactionTypeException extends RuntimeException{
    public InvalidTransactionTypeException(){
        super("Invalid transaction type");
    }
}
