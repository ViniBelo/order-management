package br.com.devpasso.order_management.api.mapper.orders.request;

import br.com.devpasso.order_management.api.dto.orders.request.AddItemToOrderRequest;
import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class AddItemToOrderRequestMapper {
    public AddItemToOrderCommand toCommand(UUID orderId, AddItemToOrderRequest request) {
        return new AddItemToOrderCommand(
                orderId,
                request.productId(),
                request.quantity()
        );
    }
}
