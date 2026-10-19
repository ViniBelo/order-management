package br.com.devpasso.order_management.application.service.products;

import br.com.devpasso.order_management.application.usecase.products.DeleteProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;

public class DeleteProductsService implements DeleteProductUseCase {
    private final ProductRepositoryPort productRepositoryPort;

    public DeleteProductsService(ProductRepositoryPort productRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void execute(String id) {
        productRepositoryPort.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found for ID: " + id));
        productRepositoryPort.deleteById(id);
    }
}
