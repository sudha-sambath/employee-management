package com.ems.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,Object> handleNotFound(ResourceNotFoundException ex){

        Map<String,Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("message",ex.getMessage());
        error.put("status","404");

        return error;

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,Object>> handleGeneric(Exception e){
        Map<String,Object> error = new HashMap<>();
        error.put("timestamp",LocalDateTime.now());
        error.put("message","Something went wrong");
        error.put("status","500");

        return new ResponseEntity<>(error,HttpStatus.INTERNAL_SERVER_ERROR);
    }


  /*  Feature,	MethodArgumentNotValidException	, ConstraintViolationException
    Validation Target ->	Request Body DTO	, Method parameters
    Annotation Used	  -> @Valid / @Validated ,	@Validated
    Typical Input	 -> JSON request body	, Query params / path variables
    Example  ->	@RequestBody EmployeeRequest ,	@RequestParam, @PathVariable*/
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> handleValidation(MethodArgumentNotValidException ex){
        Map<String,Object> errors = new HashMap<>();
        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(),error.getDefaultMessage()));

        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);

    }

    /*When validation fails on method parameters, such as:
    @RequestParam, @PathVariable, @RequestHeader,service method parameters*/
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String,String>> handleConstraintViolation(ConstraintViolationException ex){
        Map<String,String> errors = new HashMap<>();

        errors.put("message", ex.getMessage());
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
