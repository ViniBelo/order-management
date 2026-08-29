package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.usecase.DeleteProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.ProductRepository;

public class DeleteProductsService implements DeleteProductUseCase {
    private final ProductRepository productRepository;

    public DeleteProductsService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void execute(String id) {
        productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
        productRepository.deleteById(id);
    }
}
