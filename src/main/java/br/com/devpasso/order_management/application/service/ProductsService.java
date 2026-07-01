package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.model.Product;

public class ProductsService implements ListProductsUseCase {
    private final ProductRepository productRepository;

    public ProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PaginatedResult<Product> execute(PaginationQuery paginationQuery, String name) {
        return productRepository.findAllByNameContainingIgnoreCase(paginationQuery, name);
    }
}
