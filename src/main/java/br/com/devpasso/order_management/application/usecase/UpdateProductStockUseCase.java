package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.ProductResult;

public interface UpdateProductStockUseCase {
    ProductResult execute(String id, UpdateProductStockCommand command);
}
