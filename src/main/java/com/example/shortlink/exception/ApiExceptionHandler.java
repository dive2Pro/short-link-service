package com.example.shortlink.exception;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice 
public class ApiExceptionHandler {
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<Map<String, String>> handleValidation(
              MethodArgumentNotValidException exception
      ) {
          Map<String, String> errors = exception.getBindingResult()
                  .getFieldErrors()
                  .stream()
                  .collect(Collectors.toMap(
                          FieldError::getField,
                          FieldError::getDefaultMessage,
                          (first, ignored) -> first));

          return ResponseEntity
                  .status(HttpStatus.BAD_REQUEST)
                  .body(errors);
    }
      
    @ExceptionHandler (LinkNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleLinkNotFoundException(LinkNotFoundException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }


    @ExceptionHandler (LinkCodeAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidRequestException(LinkCodeAlreadyExistsException ex) {
        Map<String, String> error = Map.of("error", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(error);
    }
}
