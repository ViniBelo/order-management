package br.com.devpasso.order_management.api.mapper.orders.response;

import br.com.devpasso.order_management.api.dto.response.orders.OrderResponse;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import org.springframework.stereotype.Component;

@Component
public class OrderResponseMapper {
    private final OrderItemResponseMapper orderItemResponseMapper;

    public OrderResponseMapper(OrderItemResponseMapper orderItemResponseMapper) {
        this.orderItemResponseMapper = orderItemResponseMapper;
    }

    public OrderResponse toResponse(OrderResult result) {
        return new OrderResponse(
                result.id(),
                result.customerId(),
                result.status(),
                result.items()
                        .stream()
                        .map(orderItemResponseMapper::toResponse)
                        .toList(),
                result.total(),
                result.createdAt()
        );
    }
}
