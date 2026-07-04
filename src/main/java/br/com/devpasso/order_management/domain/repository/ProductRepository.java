package br.com.devpasso.order_management.domain.repository;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    PaginatedResult<Product> findAllByNameContainingIgnoreCase(PaginationQuery paginationQuery, String name);
    Optional<Product> findById(String id);
}
