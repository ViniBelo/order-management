package br.com.devpasso.order_management.api.mapper.orders.response;

import br.com.devpasso.order_management.api.dto.response.orders.OrderItemResponse;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import org.springframework.stereotype.Component;

@Component
public class OrderItemResponseMapper {
    public OrderItemResponse toResponse(OrderItemResult result) {
        return new OrderItemResponse(
                result.id(),
                result.productName(),
                result.unitPrice(),
                result.quantity(),
                result.subtotal()
        );
    }
}
