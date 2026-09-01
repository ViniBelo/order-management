package br.com.devpasso.order_management.api.exception;

import br.com.devpasso.order_management.api.dto.exception.ErrorResponse;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 400 Bad Request: When the user passes a sort parameter that does not exist in the Entity,
     * or formats the query parameters incorrectly.
     */
    @ExceptionHandler({PropertyReferenceException.class,
            InvalidDataAccessApiUsageException.class})
    public ResponseEntity<ErrorResponse> handleInvalidSortParameter(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        "Invalid pagination or sort parameter provided.",
                        null
                ));
    }

    /**
     * Handles 400 Bad Request: When the user passes invalid payload values failing validation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        List<ErrorResponse.ValidationError> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toValidationError)
                .toList();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        "Invalid request data.",
                        errors
                ));
    }

    /**
     * Handles 400 Bad Request: When path variable format fails or parameter type mismatch occurs.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        "Bad Request",
                        "Invalid value provided.",
                        null
                ));
    }

    private ErrorResponse.ValidationError toValidationError(FieldError error) {
        assert error.getDefaultMessage() != null;
        return new ErrorResponse.ValidationError(
                error.getField(),
                error.getDefaultMessage()
        );
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String title, String detail,
                                            List<ErrorResponse.ValidationError> errors) {
        return new ErrorResponse(
                status.value(),
                title,
                detail,
                UUID.randomUUID().toString(),
                Instant.now(),
                errors
        );
    }

    /**
     * Handles 404 Not Found: When the user requests a resource that does not exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrorResponse(
                        HttpStatus.NOT_FOUND,
                        "Not Found",
                        "The requested resource could not be found.",
                        null
                ));
    }

    /**
     * Handles 409 Conflict: When the user tries to create a resource that already exists.
     */
    @ExceptionHandler(ResourceConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrorResponse(
                        HttpStatus.CONFLICT,
                        "Conflict",
                        Objects.requireNonNullElse(e.getMessage(), "The resource already exists."),
                        null
                ));
    }

    /**
     * Handles 500 Internal Server Error: The ultimate fallback for any unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllUncaughtExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Internal Server Error",
                        "An unexpected error occurred on our servers. Please try again later.",
                        null
                ));
    }
}
