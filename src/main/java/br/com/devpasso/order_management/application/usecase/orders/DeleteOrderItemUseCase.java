package br.com.devpasso.order_management.application.usecase.orders;

import java.util.UUID;

public interface DeleteOrderItemUseCase {
    void execute(UUID id, UUID itemId);
}
