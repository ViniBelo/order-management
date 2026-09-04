package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.CreateProductResult;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.CreateProductResultMapper;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;

public class CreateProductsService implements CreateProductUseCase {
    private final ProductRepository productRepository;
    private final CreateProductResultMapper createProductResultMapper;

    public CreateProductsService(ProductRepository productRepository,
                                 CreateProductResultMapper createProductResultMapper) {
        this.productRepository = productRepository;
        this.createProductResultMapper = createProductResultMapper;
    }

    @Override
    public CreateProductResult execute(CreateProductCommand command) {
        validateDuplicatedName(command.name());
        Product newProduct = command.toDomainModel();
        Product savedProduct = productRepository.save(newProduct);
        return createProductResultMapper.toResult(savedProduct);
    }

    private void validateDuplicatedName(String name) {
        if (productRepository.existsByName(name))
            throw new ResourceConflictException("Product already exists with name: " + name);
    }
}
