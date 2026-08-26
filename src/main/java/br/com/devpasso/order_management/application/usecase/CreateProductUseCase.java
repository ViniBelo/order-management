package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.domain.model.Product;

public interface CreateProductUseCase {
    Product execute(CreateProductCommand command);
}
