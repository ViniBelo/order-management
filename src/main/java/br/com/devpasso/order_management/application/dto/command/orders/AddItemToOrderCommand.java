package br.com.devpasso.order_management.application.dto.command.orders;

import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.Product;

import java.util.UUID;

public record AddItemToOrderCommand(
        UUID orderId,
        UUID productId,
        Integer quantity
) {
    public OrderItem toDomainModel(Product product) {
        return new OrderItem(
                null,
                product,
                product.getName(),
                product.getPrice(),
                quantity
        );
    }
}
