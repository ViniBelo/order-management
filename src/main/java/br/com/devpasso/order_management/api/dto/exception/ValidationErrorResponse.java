package br.com.devpasso.order_management.api.dto.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

@Schema(name = "ValidationErrorResponse", description = "Validation error payload for request validation failures")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record ValidationErrorResponse(
        @Schema(description = "HTTP status code", example = "400") int status,
        @Schema(description = "Error title", example = "Bad Request") String title,
        @Schema(description = "Human-readable error description", example = "Invalid request data.") String detail,
        @Schema(description = "Correlation identifier for tracing the request", example = "7b1e5d4a-2f38-4a73-aebf-5a7d4d721d1a") String traceId,
        @Schema(description = "Timestamp when the error was generated", example = "2026-08-31T12:00:00Z") Instant timestamp,
        @Schema(description = "Field validation details") List<ValidationErrorItem> errors
) { }

@Schema(name = "ValidationErrorItem", description = "Field-level validation error")
record ValidationErrorItem(
        @Schema(description = "Field that failed validation", example = "stockQuantity") String field,
        @Schema(description = "Validation message", example = "Stock quantity must be greater than or equal to zero") String message
) { }
