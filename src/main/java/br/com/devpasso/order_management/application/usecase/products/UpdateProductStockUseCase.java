package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.command.products.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;

import java.util.UUID;

public interface UpdateProductStockUseCase {
    ProductResult execute(UUID id, UpdateProductStockCommand command);
}
