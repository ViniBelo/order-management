package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;

public class CreateProductsService implements CreateProductUseCase {
    private final ProductRepository productRepository;

    public CreateProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product execute(CreateProductCommand command) {
        validateDuplicatedName(command.name());
        Product newProduct = command.toDomainModel();
        return productRepository.save(newProduct);
    }

    private void validateDuplicatedName(String name) {
        if (productRepository.existsByName(name))
            throw new ResourceConflictException("Product already exists with name: " + name);
    }
}
