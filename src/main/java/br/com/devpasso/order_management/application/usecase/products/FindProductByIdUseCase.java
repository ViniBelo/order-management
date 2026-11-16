package br.com.devpasso.order_management.application.usecase.products;

import br.com.devpasso.order_management.application.dto.result.products.ProductResult;

import java.util.UUID;

public interface FindProductByIdUseCase {
    ProductResult execute(UUID id);
}
