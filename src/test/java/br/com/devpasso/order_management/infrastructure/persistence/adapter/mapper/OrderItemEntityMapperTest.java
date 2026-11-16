package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderItemEntityMapperTest {
    private final ProductEntityMapper productEntityMapper = new ProductEntityMapper();
    private final ProductModelMapper productModelMapper = new ProductModelMapper();
    private final OrderItemEntityMapper orderItemEntityMapper = new OrderItemEntityMapper(
            productEntityMapper,
            productModelMapper
    );

    @Test
    @DisplayName("toEntity should return OrderItemEntity")
    public void toEntity_shouldReturnOrderItemEntity() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("10.00"),
                5,
                Instant.now()
        );

        OrderItem orderItem = new OrderItem(id,
                product,
                product.getName(),
                product.getPrice(),
                product.getStockQuantity());

        // When
        OrderItemEntity orderItemEntity = orderItemEntityMapper.toEntity(orderItem);

        // Then
        assertEquals(id, orderItemEntity.getId());
        assertEquals(product.getId(), orderItemEntity.getProduct().getId());
        assertEquals(product.getName(), orderItemEntity.getProductName());
        assertEquals(product.getPrice(), orderItemEntity.getUnitPrice());
        assertEquals(product.getStockQuantity(), orderItemEntity.getQuantity());
    }

    @Test
    @DisplayName("toDomain should return OrderItem")
    public void toDomain_shouldReturnOrderItem() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("10.00"),
                5,
                Instant.now()
        );

        ProductEntity productEntity = new ProductEntity();
        productEntity.changeId(product.getId());
        productEntity.changeName(product.getName());
        productEntity.changePrice(product.getPrice());
        productEntity.changeStockQuantity(product.getStockQuantity());
        productEntity.changeCreatedAt(product.getCreatedAt());

        OrderItemEntity orderItemEntity = new OrderItemEntity();
        orderItemEntity.changeId(id);
        orderItemEntity.changeProduct(productEntity);
        orderItemEntity.changeProductName(productEntity.getName());
        orderItemEntity.changeUnitPrice(productEntity.getPrice());
        orderItemEntity.changeQuantity(productEntity.getStockQuantity());

        // When
        OrderItem orderItem = orderItemEntityMapper.toDomain(orderItemEntity);

        // Then
        assertEquals(id, orderItem.id());
        assertEquals(product.getId(), orderItem.product().getId());
        assertEquals(product.getName(), orderItem.productName());
        assertEquals(product.getStockQuantity(), orderItem.quantity());
        assertEquals(product.getPrice(), orderItem.unitPrice());
    }
}
