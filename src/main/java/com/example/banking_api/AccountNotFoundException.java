package com.example.banking_api;

// Thrown when no account exists for a given ID or account number.
// Extends IllegalArgumentException so existing callers and tests keep working.
public class AccountNotFoundException extends IllegalArgumentException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}
