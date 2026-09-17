package br.com.devpasso.order_management.domain.common;

public record PaginationQuery (
        int page,
        int size,
        String sort
) {
    public PaginationQuery {
        if (page < 0) throw new IllegalArgumentException("Page must be >= 0");
        if (size <= 0) throw new IllegalArgumentException("Size must be > 0");
        if (size > 1000) throw new IllegalArgumentException("Size must be <= 1000");
    }
}
