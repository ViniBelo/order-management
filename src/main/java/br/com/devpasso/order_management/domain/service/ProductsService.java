package br.com.devpasso.order_management.domain.service;

import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.service.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductsService implements ListProductsUseCase {
    private final ProductRepository productRepository;
    private final ProductModelMapper mapper;

    public ProductsService(ProductRepository productRepository, ProductModelMapper mapper) {
        this.productRepository = productRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<Product> execute(Pageable pageable, String name) {
        Page<ProductEntity> productEntities =
                productRepository.findAllByNameContainingIgnoreCase(pageable, name);
        return productEntities.map(mapper::toModel);
    }
}
