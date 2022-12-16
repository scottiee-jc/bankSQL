package com.example.da_bank.Exceptions;

import org.springframework.data.relational.core.sql.In;

public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message){
        super(message);
    }
}
