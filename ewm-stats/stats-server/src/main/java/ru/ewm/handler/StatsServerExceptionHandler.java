package ru.ewm.handler;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ewm.exception.InvalidRequestException;

import java.util.stream.Collectors;


@RestControllerAdvice
public class StatsServerExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var reason = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        var apiError = ApiError.builder()
                .message("Incorrectly made request")
                .reason(reason)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler({InvalidRequestException.class})
    public ResponseEntity<ApiError> handleInvalidRequestException(InvalidRequestException e) {
        var apiError = ApiError.builder()
                .message("Incorrectly made request")
                .reason(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        var apiError = ApiError.builder()
                .message("Integrity constraint has been violated")
                .reason(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiError> handleAnyException(Throwable e) {
        var apiError = ApiError.builder()
                .message("Internal Server Error")
                .reason(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }
}
