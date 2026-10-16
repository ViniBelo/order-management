package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOrdersServiceTest {

    @Mock
    private OrderResultMapper orderResultMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FindOrdersService findOrdersService;

    @Test
    @DisplayName("Should return mapped order result when order is found by ID")
    void shouldReturnOrderResultWhenOrderExists() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        OrderItem item = new OrderItem(
                UUID.randomUUID(),
                productId,
                "Wireless Mouse",
                new BigDecimal("80.00"),
                2
        );
        Order order = new Order(
                orderId,
                customerId,
                OrderStatus.DRAFT,
                List.of(item),
                createdAt
        );

        OrderItemResult itemResult = new OrderItemResult(
                productId,
                "Wireless Mouse",
                new BigDecimal("80.00"),
                2
        );
        OrderResult expectedResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                List.of(itemResult),
                new BigDecimal("160.00"),
                createdAt
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(orderResultMapper.map(order)).thenReturn(expectedResult);

        // When
        OrderResult result = findOrdersService.execute(orderId);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(orderId, result.orderId());
        assertEquals(customerId, result.customerId());
        assertEquals("DRAFT", result.orderStatus());
        assertEquals(1, result.items().size());
        assertEquals(new BigDecimal("160.00"), result.orderTotal());
        assertEquals(createdAt, result.createdAt());

        verify(orderRepository).findById(orderId);
        verify(orderResultMapper).map(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order is not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        // Given
        UUID orderId = UUID.randomUUID();
        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> findOrdersService.execute(orderId)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoInteractions(orderResultMapper);
    }
}
