package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderItemEntityMapperTest {
    private final OrderItemEntityMapper orderItemEntityMapper = new OrderItemEntityMapper();

    @Test
    @DisplayName("toEntity should return OrderItemEntity")
    public void toEntity_shouldReturnOrderItemEntity() {
        // Given
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Product Name";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 5;

        OrderItem orderItem = new OrderItem(id,
                productId,
                productName,
                unitPrice,
                quantity);

        // When
        OrderItemEntity orderItemEntity = orderItemEntityMapper.toEntity(orderItem);

        // Then
        assertEquals(id, orderItemEntity.getId());
        assertEquals(productId, orderItemEntity.getProductId());
        assertEquals(productName, orderItemEntity.getProductName());
        assertEquals(unitPrice, orderItemEntity.getUnitPrice());
        assertEquals(quantity, orderItemEntity.getQuantity());
    }

    @Test
    @DisplayName("toDomain should return OrderItem")
    public void toDomain_shouldReturnOrderItem() {
        // Given
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Product Name";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 5;

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.changeId(id);
        orderItemEntity.changeProductId(productId);
        orderItemEntity.changeProductName(productName);
        orderItemEntity.changeUnitPrice(unitPrice);
        orderItemEntity.changeQuantity(quantity);

        // When
        OrderItem orderItem = orderItemEntityMapper.toDomain(orderItemEntity);

        // Then
        assertEquals(id, orderItem.id());
        assertEquals(productId, orderItem.productId());
        assertEquals(productName, orderItem.productName());
        assertEquals(quantity, orderItem.quantity());
        assertEquals(unitPrice, orderItem.unitPrice());
    }
}
