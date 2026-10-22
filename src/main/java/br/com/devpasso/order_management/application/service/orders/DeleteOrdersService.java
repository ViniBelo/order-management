package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;

import java.util.UUID;

public class DeleteOrdersService implements DeleteOrderUseCase {
    private final OrderRepositoryPort orderRepositoryPort;

    public DeleteOrdersService(OrderRepositoryPort orderRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
    }

    @Override
    public void execute(UUID id) {
        if (!orderRepositoryPort.existsById(id))
            throw new ResourceNotFoundException("Order not found for ID: " + id);
        orderRepositoryPort.deleteById(id);
    }
}
