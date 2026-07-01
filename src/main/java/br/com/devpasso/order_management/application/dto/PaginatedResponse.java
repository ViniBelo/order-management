package br.com.devpasso.order_management.application.dto;

import br.com.devpasso.order_management.domain.common.PaginatedResult;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
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
