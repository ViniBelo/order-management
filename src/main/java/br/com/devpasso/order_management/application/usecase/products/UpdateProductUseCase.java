package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.command.products.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;

public interface UpdateProductUseCase {
    ProductResult execute(String id, UpdateProductCommand command);
}
