package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderItemUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;

import java.util.UUID;

public class DeleteOrderItemsServiceItem implements DeleteOrderItemUseCase {
    private final OrderRepositoryPort orderRepositoryPort;
    private final ProductRepositoryPort productRepositoryPort;

    public DeleteOrderItemsServiceItem(OrderRepositoryPort orderRepositoryPort,
                                       ProductRepositoryPort productRepositoryPort) {
        this.orderRepositoryPort = orderRepositoryPort;
        this.productRepositoryPort = productRepositoryPort;
    }

    @Override
    public void execute(UUID id, UUID itemId) {
        Order order = orderRepositoryPort.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Order item not found for ID: " + id));

        OrderItem item = order.items().stream()
                .filter(orderItem -> orderItem.id().equals(itemId))
                .findFirst()
                .orElse(null);

        order.removeItem(itemId);

        orderRepositoryPort.save(order);
        if (item != null) {
            productRepositoryPort.save(item.product());
        }
    }
}
