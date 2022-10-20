package com.udemy.demo.common;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ExceptionResolver {

    @ExceptionHandler
    public ResponseEntity handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String,String> erros = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            erros.put(fieldName,errorMessage);
        });

        return  new ResponseEntity(erros, HttpStatus.BAD_REQUEST);
    }


}
