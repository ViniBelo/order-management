package br.com.devpasso.order_management.application.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedResponse<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    /**
     * Creates a paginated response by combining Spring's Page metadata
     * with a list already mapped to the response format (DTO).
     *
     * @param page          The original Page from the Use Case / Repository (Type Y)
     * @param mappedContent The list of elements converted to the target DTO (Type T)
     * @param <T>           The response DTO type (e.g., ProductResponse)
     * @param <Y>           The original entity type (e.g., Product)
     */
    public static <T, Y> PaginatedResponse<T> from(Page<Y> page, List<T> mappedContent) {
        return new PaginatedResponse<>(
                mappedContent,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
