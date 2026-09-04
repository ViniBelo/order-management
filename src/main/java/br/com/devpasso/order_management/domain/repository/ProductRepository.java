package br.com.devpasso.order_management.domain.repository;

import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    PaginatedQueryResult<Product> findAllByNameContainingIgnoreCase(PaginationQuery paginationQuery, String name);
    Optional<Product> findById(String id);
    Product save(Product product);
    boolean existsByName(String name);
    void deleteById(String id);
}
