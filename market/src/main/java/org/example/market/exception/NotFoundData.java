package org.example.market.exception;

public class NotFoundData extends RuntimeException {
    public NotFoundData(String message) {
        super(message);
    }
}
