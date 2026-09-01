package br.com.devpasso.order_management.api.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "NotFoundErrorResponse", description = "Resource not found error payload")
public record NotFoundErrorResponse(
        @Schema(description = "HTTP status code", example = "404") int status,
        @Schema(description = "Error title", example = "Not Found") String title,
        @Schema(description = "Human-readable error description", example = "The requested resource could not be found.") String detail,
        @Schema(description = "Correlation identifier for tracing the request", example = "7b1e5d4a-2f38-4a73-aebf-5a7d4d721d1a") String traceId,
        @Schema(description = "Timestamp when the error was generated", example = "2026-08-31T12:00:00Z") Instant timestamp
) { }
