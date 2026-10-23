package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderEntityMapperTest {
    private final OrderStatusMapper orderStatusMapper = new OrderStatusMapper();
    private final OrderItemEntityMapper orderItemEntityMapper = new OrderItemEntityMapper();
    private final OrderEntityMapper orderEntityMapper = new OrderEntityMapper(
            orderStatusMapper,
            orderItemEntityMapper
    );

    @Test
    @DisplayName("toEntity should return OrderEntity")
    public void toEntityShouldReturnOrderEntity() {
        // Given
        UUID customerId = UUID.randomUUID();
        final Order order = new Order(customerId);

        // When
        final OrderEntity orderEntity = orderEntityMapper.toEntity(order);

        // Then
        assertEquals(order.id(), orderEntity.getId());
        assertEquals(order.customerId(), orderEntity.getCustomerId());
        assertEquals(orderStatusMapper.toEntity(order.status()), orderEntity.getStatus());
        assertEquals(
                order.items().isEmpty() ?
                        Collections.emptyList() :
                        order.items()
                                .stream()
                                .map(orderItemEntityMapper::toEntity)
                                .toList(),
                List.of()
        );
        assertEquals(order.createdAt(), orderEntity.getCreatedAt());
    }

    @Test
    @DisplayName("toDomain should return Order")
    public void toDomainShouldReturnOrder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderStatusEntity status = OrderStatusEntity.DRAFT;
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(status);

        // When
        Order order = orderEntityMapper.toDomain(orderEntity);

        // Then
        assertEquals(orderEntity.getId(), order.id());
        assertEquals(orderEntity.getCustomerId(), order.customerId());
        assertEquals(orderStatusMapper.toDomain(orderEntity.getStatus()), order.status());
        assertEquals(
                orderEntity.getItems() == null ?
                        Collections.emptyList() :
                        orderEntity.getItems()
                                .stream()
                                .map(orderItemEntityMapper::toDomain)
                                .toList(),
                order.items()
        );
        assertEquals(orderEntity.getCreatedAt(), order.createdAt());
    }
}
