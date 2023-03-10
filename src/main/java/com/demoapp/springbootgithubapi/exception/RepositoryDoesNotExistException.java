package com.demoapp.springbootgithubapi.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class RepositoryDoesNotExistException extends RuntimeException {
    private final String username;
    private final String repositoryName;

    public RepositoryDoesNotExistException(String username, String repositoryName) {
        super(String.format("Repository '%s' for username '%s' not found", repositoryName, username));
        this.username = username;
        this.repositoryName = repositoryName;
    }
}

