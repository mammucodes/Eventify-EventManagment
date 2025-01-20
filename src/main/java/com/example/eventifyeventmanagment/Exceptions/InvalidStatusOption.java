package com.example.eventifyeventmanagment.Exceptions;

public class InvalidStatusOption extends Exception {
    private int code;
    public InvalidStatusOption(String message, int code) {
        super(message);
        this.code = code;
    }
}
