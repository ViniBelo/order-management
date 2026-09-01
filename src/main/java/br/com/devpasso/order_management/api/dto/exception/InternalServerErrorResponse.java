package br.com.devpasso.order_management.api.dto.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(name = "InternalServerErrorResponse", description = "Internal server error payload")
public record InternalServerErrorResponse(
        @Schema(description = "HTTP status code", example = "500") int status,
        @Schema(description = "Error title", example = "Internal Server Error") String title,
        @Schema(description = "Human-readable error description", example = "An unexpected error occurred on our servers. Please try again later.") String detail,
        @Schema(description = "Correlation identifier for tracing the request", example = "7b1e5d4a-2f38-4a73-aebf-5a7d4d721d1a") String traceId,
        @Schema(description = "Timestamp when the error was generated", example = "2026-08-31T12:00:00Z") Instant timestamp
) { }
