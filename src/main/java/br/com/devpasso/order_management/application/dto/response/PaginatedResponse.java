package br.com.devpasso.order_management.application.dto.response;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
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
) {
    /**
     * Creates a paginated result by combining metadata from an existing PaginatedResult
     * with a list already mapped to the target format.
     *
     * @param paginatedResult The original paginated result containing the pagination metadata.
     * @param mappedContent   The list of elements converted to the target type.
     * @param <T>             The target content type.
     * @param <Y>             The original content type.
     */
    public static <T, Y> PaginatedResponse<T> from(PaginatedResult<Y> paginatedResult, List<T> mappedContent) {
        return new PaginatedResponse<>(
                mappedContent,
                paginatedResult.page(),
                paginatedResult.size(),
                paginatedResult.totalElements(),
                paginatedResult.totalPages()
        );
    }
}
