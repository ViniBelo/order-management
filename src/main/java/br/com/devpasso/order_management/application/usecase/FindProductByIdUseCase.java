package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.application.dto.result.ProductResult;

public interface FindProductByIdUseCase {
    ProductResult execute(String id);
}
