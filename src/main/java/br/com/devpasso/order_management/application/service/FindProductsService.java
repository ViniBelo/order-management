package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.model.Product;

public class FindProductsService implements ListProductsUseCase, FindProductByIdUseCase {
    private final ProductRepository productRepository;

    public FindProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public PaginatedResult<Product> execute(PaginationQuery paginationQuery, String name) {
        return productRepository.findAllByNameContainingIgnoreCase(paginationQuery, name);
    }

    @Override
    public Product execute(String id) {
        return fetchProduct(id);
    }

    private Product fetchProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
    }
}
