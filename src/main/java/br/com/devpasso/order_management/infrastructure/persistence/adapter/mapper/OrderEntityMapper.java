package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class OrderEntityMapper {
    private final OrderStatusMapper orderStatusMapper;
    private final OrderItemEntityMapper orderItemEntityMapper;

    public OrderEntityMapper(OrderStatusMapper orderStatusMapper,
                             OrderItemEntityMapper orderItemEntityMapper) {
        this.orderStatusMapper = orderStatusMapper;
        this.orderItemEntityMapper = orderItemEntityMapper;
    }

    public OrderEntity toEntity(Order order) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(order.id());
        orderEntity.changeCustomerId(order.customerId());
        orderEntity.changeStatus(orderStatusMapper.toEntity(order.status()));
        orderEntity.changeItems(order.items()
                .stream()
                .map(orderItemEntityMapper::toEntity)
                .toList());
        orderEntity.changeCreatedAt(order.createdAt());
        return orderEntity;
    }

    public Order toDomain(OrderEntity orderEntity) {
        return new Order(orderEntity.getId(),
                orderEntity.getCustomerId(),
                orderStatusMapper.toDomain(orderEntity.getStatus()),
                orderEntity.getItems()
                        .stream()
                        .map(orderItemEntityMapper::toDomain)
                        .collect(Collectors.toList()),
                orderEntity.getCreatedAt());
    }
}
