package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.domain.model.Order;
import org.springframework.stereotype.Component;

@Component
public class OrderResultMapper {
    private final OrderItemResultMapper orderItemResultMapper;

    public OrderResultMapper(OrderItemResultMapper orderItemResultMapper) {
        this.orderItemResultMapper = orderItemResultMapper;
    }

    public OrderResult map(Order order) {
        return new OrderResult(
                order.id(),
                order.customerId(),
                order.status().toString(),
                order.items()
                        .stream()
                        .map(orderItemResultMapper::map)
                        .toList(),
                order.total(),
                order.createdAt()
        );
    }
}
