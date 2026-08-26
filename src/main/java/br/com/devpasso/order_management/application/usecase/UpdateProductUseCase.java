package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.domain.model.Product;

public interface UpdateProductUseCase {
    Product execute(String id, UpdateProductCommand command);
}
