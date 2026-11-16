package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderItemUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;

import java.util.UUID;

public class DeleteOrderItemsServiceItem implements DeleteOrderItemUseCase {
    private final OrderRepositoryPort orderRepositoryPort;

    public DeleteOrderItemsServiceItem(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public void execute(UUID id, UUID itemId) {
        Order order = orderRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found for ID: " + id));

        order.removeItem(itemId);

        orderRepositoryPort.save(order);
    }
}
