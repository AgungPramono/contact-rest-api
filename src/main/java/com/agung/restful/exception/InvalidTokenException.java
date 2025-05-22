package com.agung.restful.exception;

public class InvalidTokenException extends RuntimeException{
    public InvalidTokenException(String message) {
        super(message);
    }
}
