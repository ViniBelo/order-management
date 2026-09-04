package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.result.ProductResult;

public interface UpdateProductUseCase {
    ProductResult execute(String id, UpdateProductCommand command);
}
