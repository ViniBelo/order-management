package br.com.devpasso.order_management.domain.common;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginatedResult<T>(
        List<T> content,
        int page,
        int size,
        long totalElements,
        int totalPages
) { }
