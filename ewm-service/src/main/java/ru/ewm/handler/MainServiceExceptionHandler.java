package ru.ewm.handler;

import jakarta.persistence.EntityListeners;
import lombok.Builder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.ewm.exception.ConditionMismatchException;
import ru.ewm.exception.InvalidRequestException;
import ru.ewm.exception.NotFoundException;
import ru.ewm.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MainServiceExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        var message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        var apiError = ApiError.builder()
                .reason("Incorrectly made request")
                .message(message)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        var apiError = ApiError.builder()
                .reason("The required object was not found")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(apiError);
    }

    @ExceptionHandler(ConditionMismatchException.class)
    public ResponseEntity<ApiError> handleConditionMismatchException(ConditionMismatchException e) {
        var apiError = ApiError.builder()
                .reason("For the requested operation the conditions are not met")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        var apiError = ApiError.builder()
                .reason("Integrity constraint has been violated")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException e) {
        var apiError = ApiError.builder()
                .reason("For the requested operation the conditions are not met")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(apiError);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiError> handleInvalidRequestException(InvalidRequestException e) {
        var apiError = ApiError.builder()
                .reason("Incorrectly made request")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(apiError);
    }

    @ExceptionHandler
    public ResponseEntity<ApiError> handleInternalServerErrorException(Throwable e) {
        var apiError = ApiError.builder()
                .reason("Internal server error")
                .message(e.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(apiError);
    }

    @EntityListeners(AuditingEntityListener.class)
    @Builder
    public static class ApiError {
        private String reason;
        private String message;
        @CreatedDate
        private LocalDateTime timestamp;
    }
}
