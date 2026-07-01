package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.model.Product;
import org.springframework.data.domain.Pageable;

public class ProductsService implements ListProductsUseCase {
    private final ProductRepository productRepository;
    private final WebPaginationMapper paginationMapper;

    public ProductsService(ProductRepository productRepository,
                           WebPaginationMapper paginationMapper) {
        this.productRepository = productRepository;
        this.paginationMapper = paginationMapper;
    }

    @Override
    public PaginatedResult<Product> execute(Pageable pageable, String name) {
        return productRepository.findAllByNameContainingIgnoreCase(paginationMapper.toDomainQuery(pageable), name);
    }
}
