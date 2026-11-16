package br.com.devpasso.order_management.application.dto.command.orders;

import br.com.devpasso.order_management.domain.model.Order;

import java.util.UUID;

public record CreateOrderCommand(
        UUID customerId
) {
    public Order toDomainModel() {
        return new Order(customerId);
    }
}
