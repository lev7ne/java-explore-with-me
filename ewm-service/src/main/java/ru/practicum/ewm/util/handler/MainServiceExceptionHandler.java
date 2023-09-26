package ru.practicum.ewm.util.handler;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.util.exception.ConditionMismatchException;
import ru.practicum.ewm.util.exception.InvalidRequestException;
import ru.practicum.ewm.util.exception.NotFoundException;
import ru.practicum.ewm.util.exception.ValidationException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class MainServiceExceptionHandler {
//    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return new ApiError(HttpStatus.BAD_REQUEST,
                "Incorrectly made request",
                errors,
                LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException ex) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(ApiError
                        .builder()
                        .status(status)
                        .reason("The required object was not found")
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(ConditionMismatchException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConditionMismatchException(ConditionMismatchException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ApiError
                .builder()
                .status(status)
                .reason("For the requested operation the conditions are not met")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleInvalidRequestException(InvalidRequestException ex) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return ApiError
                .builder()
                .status(status)
                .reason("Incorrectly made request")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ApiError
                .builder()
                .status(status)
                .reason("Integrity constraint has been violated")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleValidationException(ValidationException ex) {
        HttpStatus status = HttpStatus.CONFLICT;
        return ApiError
                .builder()
                .status(status)
                .reason("For the requested operation the conditions are not met")
                .message(ex.getMessage())
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerErrorException(Throwable ex) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ApiError
                .builder()
                .status(status)
                .reason("Internal server error")
                .message(ex.getMessage())
                .build();
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiError {
        private HttpStatus status;
        private String reason;
        private String message;
        @Builder.Default
        private LocalDateTime timestamp = LocalDateTime.now();
    }
}
