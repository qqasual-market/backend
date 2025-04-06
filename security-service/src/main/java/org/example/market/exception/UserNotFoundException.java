package org.example.market.exception;

public class UserNotFoundException extends RuntimeException {
public UserNotFoundException(String message) {
    super(message);
}
}
