package br.com.devpasso.order_management.application.usecase.products;

import java.util.UUID;

public interface DeleteProductUseCase {
    void execute(UUID id);
}
