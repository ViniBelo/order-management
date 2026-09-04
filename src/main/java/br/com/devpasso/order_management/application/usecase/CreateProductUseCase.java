package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.CreateProductResult;

public interface CreateProductUseCase {
    CreateProductResult execute(CreateProductCommand command);
}
