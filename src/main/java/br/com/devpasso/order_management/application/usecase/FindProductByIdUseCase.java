package br.com.devpasso.order_management.application.usecase;

import br.com.devpasso.order_management.domain.model.Product;

public interface FindProductByIdUseCase {
    Product execute(String id);
}
