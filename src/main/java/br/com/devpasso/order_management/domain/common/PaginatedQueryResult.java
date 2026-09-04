package br.com.devpasso.order_management.domain.common;

import java.util.List;

public record PaginatedQueryResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) { }
