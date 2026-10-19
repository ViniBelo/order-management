package br.com.devpasso.order_management.api.mapper.orders.request;

import br.com.devpasso.order_management.api.dto.orders.request.CreateOrderRequest;
import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderRequestMapper {
    public CreateOrderCommand toCommand(CreateOrderRequest request) {
        return new CreateOrderCommand(request.customerId());
    }
}
