package br.com.devpasso.order_management.application.dto.command.orders;

import br.com.devpasso.order_management.domain.model.OrderItem;

import java.math.BigDecimal;
import java.util.UUID;

public record AddItemToOrderCommand(
        UUID orderId,
        UUID productId,
        Integer quantity
) {
    public OrderItem toDomainModel(String productName, BigDecimal unitPrice) {
        return new OrderItem(
                null,
                productId,
                productName,
                unitPrice,
                quantity
        );
    }
}
