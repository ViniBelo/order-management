package br.com.devpasso.order_management.application.service.products;

import br.com.devpasso.order_management.application.dto.command.products.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.domain.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.products.ProductResultMapper;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;

public class UpdateProductsService implements UpdateProductUseCase, UpdateProductStockUseCase {
    private final ProductRepositoryPort productRepositoryPort;
    private final ProductResultMapper productResultMapper;

    public UpdateProductsService(ProductRepositoryPort productRepositoryPort,
                                 ProductResultMapper productResultMapper) {
        this.productRepositoryPort = productRepositoryPort;
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
        Product updatedProduct = productRepositoryPort.save(product);
        return productResultMapper.toResult(updatedProduct);
    }

    private void validateDuplicatedName(String name) {
        if (productRepositoryPort.existsByName(name))
            throw new ResourceConflictException("Product already exists with name: " + name);
    }

    @Override
    public ProductResult execute(String id, UpdateProductStockCommand command) {
        Product product = fetchProduct(id);

        product.changeStockQuantity(command.stockQuantity());
        Product updatedProduct = productRepositoryPort.save(product);
        return productResultMapper.toResult(updatedProduct);
    }

    private Product fetchProduct(String id) {
        return productRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
    }
}
