package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.domain.model.OrderItem;
import org.springframework.stereotype.Component;

@Component
public class OrderItemResultMapper {
    public OrderItemResult map(OrderItem item) {
        return new OrderItemResult(
                item.productId(),
                item.productName(),
                item.unitPrice(),
                item.quantity()
        );
    }
}
