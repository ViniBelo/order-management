package br.com.devpasso.order_management.application.dto.result;

import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    /**
     * Creates a paginated result by combining metadata from an existing PaginatedQueryResult
     * with a list already mapped to the target format.
     *
     * @param paginatedQueryResult The original paginated result containing the pagination metadata.
     * @param mappedContent   The list of elements converted to the target type.
     * @param <T>             The target content type.
     * @param <Y>             The original content type.
     */
    public static <T, Y> PaginatedResult<T> from(PaginatedQueryResult<Y> paginatedQueryResult, List<T> mappedContent) {
        return new PaginatedResult<>(
                mappedContent,
                paginatedQueryResult.page(),
                paginatedQueryResult.size(),
                paginatedQueryResult.totalElements(),
                paginatedQueryResult.totalPages()
        );
    }
}
