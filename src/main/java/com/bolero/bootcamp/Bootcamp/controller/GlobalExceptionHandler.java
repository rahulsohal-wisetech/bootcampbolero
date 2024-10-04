package com.bolero.bootcamp.Bootcamp.controller;

import com.bolero.bootcamp.Bootcamp.constant.Constants;
import com.bolero.bootcamp.Bootcamp.exception.DepartmentNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.EmployeeNotFoundException;
import com.bolero.bootcamp.Bootcamp.exception.InvalidDepartmentException;
import com.bolero.bootcamp.Bootcamp.exception.InvalidEmployeeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Objects;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();
        log.error(Constants.EXCEPTION_OCCURRED, errorMsg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMsg);
    }

    @ExceptionHandler(DepartmentNotFoundException.class)
    public ResponseEntity<String> handleDepartmentNotFoundException(DepartmentNotFoundException ex) {
        log.error(Constants.EXCEPTION_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidDepartmentException.class)
    public ResponseEntity<String> handleInvalidDepartmentException(InvalidDepartmentException ex) {
        log.error(Constants.EXCEPTION_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<String> handleEmployeeNotFoundException(EmployeeNotFoundException ex) {
        log.error(Constants.EXCEPTION_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(InvalidEmployeeException.class)
    public ResponseEntity<String> handleInvalidEmployeeException(InvalidEmployeeException ex) {
        log.error(Constants.EXCEPTION_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        log.error(Constants.EXCEPTION_OCCURRED, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ex.getMessage());
    }
}
