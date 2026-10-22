package br.com.devpasso.order_management.application.usecase.orders;

import java.util.UUID;

public interface DeleteOrderUseCase {
    void execute(UUID id);
}
