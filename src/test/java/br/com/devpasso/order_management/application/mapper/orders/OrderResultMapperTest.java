package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResultMapperTest {

    @Mock
    private OrderItemResultMapper orderItemResultMapper;

    @InjectMocks
    private OrderResultMapper mapper;

    @Test
    @DisplayName("Should map Order with items to OrderResult")
    void shouldMapOrderWithItemsToCreateOrderResult() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        UUID productId = UUID.randomUUID();

        OrderItem item = new OrderItem(
                UUID.randomUUID(),
                productId,
                "Monitor 4K",
                new BigDecimal("2500.00"),
                1
        );

        List<OrderItem> items = new ArrayList<>(List.of(item));

        Order order = new Order(
                orderId,
                customerId,
                OrderStatus.DRAFT,
                items,
                createdAt
        );

        OrderItemResult itemResult = new OrderItemResult(
                productId,
                "Monitor 4K",
                new BigDecimal("2500.00"),
                1,
                new BigDecimal("2500.00")
        );

        when(orderItemResultMapper.map(item)).thenReturn(itemResult);

        // When
        OrderResult result = mapper.map(order);

        // Then
        assertNotNull(result);
        assertEquals(orderId, result.id());
        assertEquals(customerId, result.customerId());
        assertEquals("DRAFT", result.status());
        assertEquals(1, result.items().size());
        assertEquals(itemResult, result.items().getFirst());
        assertEquals(new BigDecimal("2500.00"), result.total());
        assertEquals(createdAt, result.createdAt());

        verify(orderItemResultMapper).map(item);
    }

    @Test
    @DisplayName("Should map empty Order to OrderResult")
    void shouldMapEmptyOrderToCreateOrderResult() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        // When
        OrderResult result = mapper.map(order);

        // Then
        assertNotNull(result);
        assertEquals(order.id(), result.id());
        assertEquals(customerId, result.customerId());
        assertEquals("DRAFT", result.status());
        assertNotNull(result.items());
        assertTrue(result.items().isEmpty());
        assertEquals(BigDecimal.ZERO, result.total());
        assertEquals(order.createdAt(), result.createdAt());

        verifyNoInteractions(orderItemResultMapper);
    }
}
