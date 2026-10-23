package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.command.products.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;

import java.util.UUID;

public interface UpdateProductUseCase {
    ProductResult execute(UUID id, UpdateProductCommand command);
}
