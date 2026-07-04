package br.com.devpasso.order_management.api.exception;

import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Hidden
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles 400 Bad Request: When the user passes a sort parameter that does not exist in the Entity,
     * or formats the query parameters incorrectly.
     */
    @ExceptionHandler({PropertyReferenceException.class, InvalidDataAccessApiUsageException.class})
    public ProblemDetail handleInvalidSortParameter(RuntimeException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid pagination or sort parameter provided."
        );
        problem.setTitle("Bad Request");

        return problem;
    }

    /**
     * Handles 404 Not Found: When the user requests a resource that does not exist.
     */
    // Class that runs the exception handler
    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleResourceNotFound(RuntimeException e) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                "The requested resource could not be found."
        );
        problem.setTitle("Not Found");
        return problem;
    }

    /**
     * Handles 500 Internal Server Error: The ultimate fallback for any unhandled exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ProblemDetail handleAllUncaughtExceptions(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred on our servers. Please try again later."
        );
        problem.setTitle("Internal Server Error");

        return problem;
    }
}
