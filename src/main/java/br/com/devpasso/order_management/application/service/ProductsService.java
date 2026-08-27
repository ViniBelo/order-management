package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.response.ProductResponse;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.model.Product;

public class ProductsService implements ListProductsUseCase,
        FindProductByIdUseCase,
        CreateProductUseCase,
        UpdateProductUseCase,
        UpdateProductStockUseCase {
    private final ProductRepository productRepository;

    public ProductsService(ProductRepository productRepository) {
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

    @Override
    public Product execute(CreateProductCommand command) {
        validateDuplicatedName(command.name());
        Product newProduct = command.toDomainModel();
        return productRepository.save(newProduct);
    }

    @Override
    public Product execute(String id, UpdateProductCommand command) {
        Product product = fetchProduct(id);

        if (!product.getName().equals(command.name())) {
            validateDuplicatedName(command.name());
            product.changeName(command.name());
        }

        product.changeDescription(command.description());
        product.changePrice(command.price());
        return productRepository.save(product);
    }

    @Override
    public Product execute(String id, UpdateProductStockCommand command) {
        Product product = fetchProduct(id);

        product.changeStockQuantity(command.stockQuantity());
        return productRepository.save(product);
    }

    private Product fetchProduct(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
    }

    private void validateDuplicatedName(String name) {
        if (productRepository.existsByName(name))
            throw new ResourceConflictException("Product already exists with name: " + name);
    }
}
