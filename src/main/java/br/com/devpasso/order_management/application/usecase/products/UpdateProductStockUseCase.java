package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.command.products.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;

public interface UpdateProductStockUseCase {
    ProductResult execute(String id, UpdateProductStockCommand command);
}
