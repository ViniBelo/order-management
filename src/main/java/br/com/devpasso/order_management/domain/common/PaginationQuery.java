package br.com.devpasso.order_management.domain.common;

public record PaginationQuery (int page,
                               int size,
                               String sort) { }
