package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.result.products.ProductResult;

public interface FindProductByIdUseCase {
    ProductResult execute(String id);
}
