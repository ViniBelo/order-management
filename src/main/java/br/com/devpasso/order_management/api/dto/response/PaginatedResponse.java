package br.com.devpasso.order_management.api.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PaginatedResponse<T>(
        @Schema(description = "List of items in the current page")
        List<T> content,
        @Schema(description = "Current page number (0-based)", example = "0")
        int page,
        @Schema(description = "Page size", example = "20")
        int size,
        @Schema(description = "Total number of elements", example = "100")
        long totalElements,
        @Schema(description = "Total number of pages", example = "5")
        int totalPages
) { }
