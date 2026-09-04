package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.application.mapper.ProductResultMapper;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.model.Product;

import java.util.List;

public class FindProductsService implements ListProductsUseCase, FindProductByIdUseCase {
    private final ProductRepository productRepository;
    private final ProductResultMapper productResultMapper;

    public FindProductsService(ProductRepository productRepository,
                               ProductResultMapper productResultMapper) {
        this.productRepository = productRepository;
        this.productResultMapper = productResultMapper;
    }

    @Override
    public PaginatedResult<ProductResult> execute(PaginationQuery paginationQuery, String name) {
        PaginatedQueryResult<Product> products = productRepository.findAllByNameContainingIgnoreCase(paginationQuery, name);
        List<ProductResult> productsResult = products.content()
                .stream()
                .map(productResultMapper::toResult)
                .toList();
        return PaginatedResult.from(products, productsResult);
    }

    @Override
    public ProductResult execute(String id) {
        Product product = fetchProduct(id);
        return productResultMapper.toResult(product);
    }

    private Product fetchProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
    }
}
