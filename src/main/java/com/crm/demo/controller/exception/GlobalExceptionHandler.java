package com.crm.demo.controller.exception;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(exception = ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(exception = ContactAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleContactAlreadyExistsException(ContactAlreadyExistsException ex) {

        Map<String, String> errors  = new HashMap<>();
        errors.put("name", ex.getMessage());

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Request validation failed");
        pd.setProperty("code", "VALIDATION_FAILED");
        pd.setProperty("errors", errors);

        return ResponseEntity.badRequest()
                .body(pd);
    }

    @ExceptionHandler(exception = InvalidRequestException.class)
    public ResponseEntity<ProblemDetail> handleInvalidRequestException(InvalidRequestException ex) {

        Map<String, String> errors  = new HashMap<>();
        errors.put("name", ex.getMessage());

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Request validation failed");
        pd.setProperty("code", "VALIDATION_FAILED");
        pd.setProperty("errors", errors);

        return ResponseEntity.badRequest()
                .body(pd);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(MethodArgumentNotValidException ex) {

        Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                .collect(
                        Collectors.toMap(
                                FieldError::getField,
                                DefaultMessageSourceResolvable::getDefaultMessage,
                                (a, b) -> a
                        )
                );

        ProblemDetail pd = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
        pd.setTitle("Validation failed");
        pd.setDetail("Request validation failed");
        pd.setProperty("code", "VALIDATION_FAILED");
        pd.setProperty("errors", errors);

        return ResponseEntity.badRequest()
                .body(pd);
    }

}
