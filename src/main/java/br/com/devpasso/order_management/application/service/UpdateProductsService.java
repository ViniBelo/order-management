package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.ProductResultMapper;
import br.com.devpasso.order_management.application.usecase.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;

public class UpdateProductsService implements UpdateProductUseCase, UpdateProductStockUseCase {
    private final ProductRepository productRepository;
    private final ProductResultMapper productResultMapper;

    public UpdateProductsService(ProductRepository productRepository,
                                 ProductResultMapper productResultMapper) {
        this.productRepository = productRepository;
        this.productResultMapper = productResultMapper;
    }

    @Override
    public ProductResult execute(String id, UpdateProductCommand command) {
        Product product = fetchProduct(id);

        if (!product.getName().equals(command.name())) {
            validateDuplicatedName(command.name());
            product.changeName(command.name());
        }

        product.changeDescription(command.description());
        product.changePrice(command.price());
        Product updatedProduct = productRepository.save(product);
        return productResultMapper.toResult(updatedProduct);
    }

    private void validateDuplicatedName(String name) {
        if (productRepository.existsByName(name))
            throw new ResourceConflictException("Product already exists with name: " + name);
    }

    @Override
    public ProductResult execute(String id, UpdateProductStockCommand command) {
        Product product = fetchProduct(id);

        product.changeStockQuantity(command.stockQuantity());
        Product updatedProduct = productRepository.save(product);
        return productResultMapper.toResult(updatedProduct);
    }

    private Product fetchProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
    }
}
