package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.command.products.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.products.CreateProductResult;

public interface CreateProductUseCase {
    CreateProductResult execute(CreateProductCommand command);
}
