package br.com.devpasso.order_management.application.service.products;

import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.application.mapper.products.ProductResultMapper;
import br.com.devpasso.order_management.application.usecase.products.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.products.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import br.com.devpasso.order_management.domain.model.Product;

import java.util.List;
import java.util.UUID;

public class FindProductsService implements ListProductsUseCase, FindProductByIdUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductResultMapper productResultMapper;

    public FindProductsService(ProductRepositoryPort productRepositoryPort,
                               ProductResultMapper productResultMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.productResultMapper = productResultMapper;
    }

    @Override
    public PaginatedResult<ProductResult> execute(PaginationQuery paginationQuery, String name) {
        PaginatedQueryResult<Product> products = productRepositoryPort.findAllByNameContainingIgnoreCase(paginationQuery, name);
        List<ProductResult> productsResult = products.content()
                .stream()
                .map(productResultMapper::toResult)
                .toList();
        return PaginatedResult.from(products, productsResult);
    }

    @Override
    public ProductResult execute(UUID id) {
        Product product = fetchProduct(id);
        return productResultMapper.toResult(product);
    }

    private Product fetchProduct(UUID id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
    }
}
