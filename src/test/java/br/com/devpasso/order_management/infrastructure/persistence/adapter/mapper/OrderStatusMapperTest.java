package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderStatusMapperTest {
    private final OrderStatusMapper orderStatusMapper = new OrderStatusMapper();

    @Test
    @DisplayName("toEntity should return OrderStatusEntity")
    public void toEntity_shouldReturnOrderStatusEntity() {
        // Given
        OrderStatus orderStatus = OrderStatus.DRAFT;

        // When
        OrderStatusEntity orderStatusEntity = orderStatusMapper.toEntity(orderStatus);

        // Then
        assertEquals(OrderStatusEntity.DRAFT, orderStatusEntity);
    }

    @Test
    @DisplayName("toDomain should return OrderStatusEntity")
    public void toDomain_shouldReturnOrderStatusEntity() {
        // Given
        OrderStatusEntity orderStatusEntity = OrderStatusEntity.DRAFT;

        // When
        OrderStatus orderStatus = orderStatusMapper.toDomain(orderStatusEntity);

        // Then
        assertEquals(OrderStatus.DRAFT, orderStatus);
    }
}
