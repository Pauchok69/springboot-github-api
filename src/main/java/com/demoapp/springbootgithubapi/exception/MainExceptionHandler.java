package com.demoapp.springbootgithubapi.exception;

import com.demoapp.springbootgithubapi.client.exception.UserDoesNotExistException;
import com.demoapp.springbootgithubapi.dto.ErrorDetailsDTO;
import org.springframework.http.*;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class MainExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(UserDoesNotExistException.class)
    public ResponseEntity<ErrorDetailsDTO> handleUserDoesNotExistException(
            UserDoesNotExistException ex
    ) {
        ErrorDetailsDTO errorDetails = new ErrorDetailsDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        MediaType contentType = ex.getContentType();

        if (contentType != null && contentType.equals(MediaType.APPLICATION_XML)) {
            ErrorDetailsDTO errorDetailsDTO = new ErrorDetailsDTO(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage());

            return new ResponseEntity<>(errorDetailsDTO, HttpStatus.NOT_ACCEPTABLE);
        }

        return super.handleHttpMediaTypeNotSupported(ex, headers, status, request);
    }
}
