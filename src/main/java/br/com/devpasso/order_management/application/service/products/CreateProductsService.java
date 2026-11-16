package br.com.devpasso.order_management.application.service.products;

import br.com.devpasso.order_management.application.dto.command.products.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.products.CreateProductResult;
import br.com.devpasso.order_management.domain.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.products.CreateProductResultMapper;
import br.com.devpasso.order_management.application.usecase.products.CreateProductUseCase;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;

public class CreateProductsService implements CreateProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final CreateProductResultMapper createProductResultMapper;

    public CreateProductsService(ProductRepositoryPort productRepositoryPort,
                                 CreateProductResultMapper createProductResultMapper) {
        this.productRepositoryPort = productRepositoryPort;
        this.createProductResultMapper = createProductResultMapper;
    }

    @Override
    public CreateProductResult execute(CreateProductCommand command) {
        validateDuplicatedName(command.name());
        Product newProduct = command.toDomainModel();
        Product savedProduct = productRepositoryPort.save(newProduct);
        return createProductResultMapper.toResult(savedProduct);
    }

    private void validateDuplicatedName(String name) {
        if (productRepositoryPort.existsByName(name))
            throw new ResourceConflictException("Product already exists with name: " + name);
    }
}
