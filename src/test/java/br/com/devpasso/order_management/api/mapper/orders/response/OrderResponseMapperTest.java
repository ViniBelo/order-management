package br.com.devpasso.order_management.api.mapper.orders.response;

import br.com.devpasso.order_management.api.dto.orders.response.OrderItemResponse;
import br.com.devpasso.order_management.api.dto.orders.response.OrderResponse;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderResponseMapperTest {

    @Mock
    private OrderItemResponseMapper orderItemResponseMapper;

    @InjectMocks
    private OrderResponseMapper orderResponseMapper;

    @Test
    @DisplayName("Should map OrderResult with empty items to OrderResponse successfully")
    void shouldMapOrderResultWithEmptyItemsToOrderResponse() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        OrderResult orderResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        // When
        OrderResponse response = orderResponseMapper.toResponse(orderResult);

        // Then
        assertNotNull(response);
        assertEquals(orderId, response.id());
        assertEquals(customerId, response.customerId());
        assertEquals("DRAFT", response.status());
        assertTrue(response.items().isEmpty());
        assertEquals(BigDecimal.ZERO, response.total());
        assertEquals(createdAt, response.createdAt());

        verifyNoInteractions(orderItemResponseMapper);
    }

    @Test
    @DisplayName("Should map OrderResult with items to OrderResponse successfully")
    void shouldMapOrderResultWithItemsToOrderResponse() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        OrderItemResult itemResult = new OrderItemResult(
                itemId,
                "USB-C Cable",
                new BigDecimal("15.00"),
                2,
                new BigDecimal("30.00")
        );

        OrderItemResponse itemResponse = new OrderItemResponse(
                itemId,
                "USB-C Cable",
                new BigDecimal("15.00"),
                2,
                new BigDecimal("30.00")
        );

        OrderResult orderResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                List.of(itemResult),
                new BigDecimal("30.00"),
                createdAt
        );

        when(orderItemResponseMapper.toResponse(itemResult)).thenReturn(itemResponse);

        // When
        OrderResponse response = orderResponseMapper.toResponse(orderResult);

        // Then
        assertNotNull(response);
        assertEquals(orderId, response.id());
        assertEquals(customerId, response.customerId());
        assertEquals("DRAFT", response.status());
        assertEquals(1, response.items().size());
        assertEquals(itemResponse, response.items().getFirst());
        assertEquals(new BigDecimal("30.00"), response.total());
        assertEquals(createdAt, response.createdAt());

        verify(orderItemResponseMapper).toResponse(itemResult);
    }
}
