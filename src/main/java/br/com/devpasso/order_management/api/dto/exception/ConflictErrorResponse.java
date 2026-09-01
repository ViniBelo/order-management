package br.com.devpasso.order_management.api.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "ConflictErrorResponse", description = "Conflict error payload")
public record ConflictErrorResponse(
        @Schema(description = "HTTP status code", example = "409") int status,
        @Schema(description = "Error title", example = "Conflict") String title,
        @Schema(description = "Human-readable error description", example = "A product with this name already exists.") String detail,
        @Schema(description = "Correlation identifier for tracing the request", example = "7b1e5d4a-2f38-4a73-aebf-5a7d4d721d1a") String traceId,
        @Schema(description = "Timestamp when the error was generated", example = "2026-08-31T12:00:00Z") Instant timestamp
) { }
