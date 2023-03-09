package com.backend.microservice.exception;

import com.backend.microservice.model.ErrorDTO;
import com.backend.microservice.model.Response;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Response handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        Response response = new Response();
        List<ErrorDTO> errors = new ArrayList<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> {
                    ErrorDTO errorDTO = new ErrorDTO(error.getField(), error.getDefaultMessage());
                    errors.add(errorDTO);
                });
        response.setHttpStatus(HttpStatus.BAD_REQUEST);
        response.setErrors(errors);
        return response;
    }
}
