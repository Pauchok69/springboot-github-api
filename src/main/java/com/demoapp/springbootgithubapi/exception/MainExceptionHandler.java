package com.demoapp.springbootgithubapi.exception;

import com.demoapp.springbootgithubapi.client.exception.UserDoesNotExistException;
import com.demoapp.springbootgithubapi.dto.ErrorDetailsDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MainExceptionHandler {
    @ExceptionHandler(UserDoesNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDetailsDTO handleUserDoesNotExistException(
            UserDoesNotExistException ex
    ) {
        return new ErrorDetailsDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
    public ErrorDetailsDTO handleHttpMediaTypeNotSupportedException(
            HttpMediaTypeNotSupportedException ex
    ) {
        return new ErrorDetailsDTO(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());
    }
}
