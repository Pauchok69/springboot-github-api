package com.demoapp.springbootgithubapi.client.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserDoesNotExistException extends RuntimeException {
    private final String username;

    public UserDoesNotExistException(String username) {
        super(String.format("User with username '%s' not found", username));
        this.username = username;
    }
}
