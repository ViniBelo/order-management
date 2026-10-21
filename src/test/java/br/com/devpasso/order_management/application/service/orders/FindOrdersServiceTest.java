package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.FilterOrdersByStatusCommand;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindOrdersServiceTest {

    @Mock
    private OrderResultMapper orderResultMapper;

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

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
                2,
                new BigDecimal("160.00")
        );
        OrderResult expectedResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                List.of(itemResult),
                new BigDecimal("160.00"),
                createdAt
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(orderResultMapper.map(order)).thenReturn(expectedResult);

        // When
        OrderResult result = findOrdersService.execute(orderId);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(orderId, result.id());
        assertEquals(customerId, result.customerId());
        assertEquals("DRAFT", result.status());
        assertEquals(1, result.items().size());
        assertEquals(new BigDecimal("160.00"), result.total());
        assertEquals(createdAt, result.createdAt());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderResultMapper).map(order);
    }

    @Test
    @DisplayName("Should return mapped order result when order is found with empty items")
    void shouldReturnOrderResultWhenOrderHasNoItems() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Order order = new Order(
                orderId,
                customerId,
                OrderStatus.DRAFT,
                List.of(),
                createdAt
        );

        OrderResult expectedResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                List.of(),
                BigDecimal.ZERO,
                createdAt
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(orderResultMapper.map(order)).thenReturn(expectedResult);

        // When
        OrderResult result = findOrdersService.execute(orderId);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(orderId, result.id());
        assertEquals(customerId, result.customerId());
        assertEquals("DRAFT", result.status());
        assertTrue(result.items().isEmpty());
        assertEquals(BigDecimal.ZERO, result.total());
        assertEquals(createdAt, result.createdAt());

        verify(orderRepositoryPort).findById(orderId);
        verify(orderResultMapper).map(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order is not found by ID")
    void shouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        // Given
        UUID orderId = UUID.randomUUID();
        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> findOrdersService.execute(orderId)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(orderRepositoryPort).findById(orderId);
        verifyNoInteractions(orderResultMapper);
    }

    @Test
    @DisplayName("Should return paginated order results when orders exist for status")
    void shouldReturnPaginatedOrderResultWhenOrdersExist() {
        // Given
        PaginationQuery query = new PaginationQuery(0, 10, "createdAt,ASC");
        FilterOrdersByStatusCommand command = FilterOrdersByStatusCommand.DRAFT;

        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Order order = new Order(
                orderId,
                customerId,
                OrderStatus.DRAFT,
                Collections.emptyList(),
                createdAt
        );

        OrderResult orderResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        PaginatedQueryResult<Order> paginatedQueryResult = new PaginatedQueryResult<>(
                List.of(order), 0, 10, 1, 1
        );

        when(orderRepositoryPort.findByStatus(query, OrderStatus.DRAFT))
                .thenReturn(paginatedQueryResult);
        when(orderResultMapper.map(order))
                .thenReturn(orderResult);

        // When
        PaginatedResult<OrderResult> result = findOrdersService.execute(query, command);

        // Then
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(1, result.totalPages());
        assertEquals(0, result.page());
        assertEquals(10, result.size());
        assertEquals(1, result.content().size());
        assertEquals(orderResult, result.content().getFirst());

        verify(orderRepositoryPort).findByStatus(query, OrderStatus.DRAFT);
        verify(orderResultMapper).map(order);
    }

    @Test
    @DisplayName("Should return empty paginated result when no orders match status")
    void shouldReturnEmptyPaginatedResultWhenNoOrdersFound() {
        // Given
        PaginationQuery query = new PaginationQuery(0, 10, "createdAt,ASC");
        FilterOrdersByStatusCommand command = FilterOrdersByStatusCommand.CONFIRMED;

        PaginatedQueryResult<Order> paginatedQueryResult = new PaginatedQueryResult<>(
                Collections.emptyList(), 0, 10, 0, 0
        );

        when(orderRepositoryPort.findByStatus(query, OrderStatus.CONFIRMED))
                .thenReturn(paginatedQueryResult);

        // When
        PaginatedResult<OrderResult> result = findOrdersService.execute(query, command);

        // Then
        assertNotNull(result);
        assertEquals(0, result.totalElements());
        assertEquals(0, result.totalPages());
        assertTrue(result.content().isEmpty());

        verify(orderRepositoryPort).findByStatus(query, OrderStatus.CONFIRMED);
        verifyNoInteractions(orderResultMapper);
    }
}
