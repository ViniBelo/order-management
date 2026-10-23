package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.domain.exception.InvalidOrderStateException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteOrdersServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private DeleteOrderItemsServiceItem deleteOrdersService;

    @Test
    @DisplayName("Should remove item from order and save order when order exists in draft status")
    void execute_WithExistingOrderAndItem_ShouldRemoveItemAndSaveOrder() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID itemIdToRemove = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        OrderItem itemToRemove = new OrderItem(
                itemIdToRemove,
                productId,
                "Keyboard",
                new BigDecimal("100.00"),
                1
        );

        List<OrderItem> items = new ArrayList<>();
        items.add(itemToRemove);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.DRAFT,
                items,
                Instant.now()
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // When
        deleteOrdersService.execute(orderId, itemIdToRemove);

        // Then
        assertTrue(order.items().isEmpty());
        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort).save(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order does not exist")
    void execute_WithNonExistingOrder_ShouldThrowResourceNotFoundException() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteOrdersService.execute(orderId, itemId)
        );

        assertEquals("Order item not found for ID: " + orderId, exception.getMessage());
        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort, never()).save(any());
    }

    @Test
    @DisplayName("Should throw InvalidOrderStateException when order is not in draft status")
    void execute_WithNonDraftOrder_ShouldThrowInvalidOrderStateException() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        OrderItem item = new OrderItem(
                itemId,
                productId,
                "Keyboard",
                new BigDecimal("100.00"),
                1
        );

        List<OrderItem> items = new ArrayList<>();
        items.add(item);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.CONFIRMED,
                items,
                Instant.now()
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));

        // When & Then
        InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> deleteOrdersService.execute(orderId, itemId)
        );

        assertEquals("Order is not in draft status", exception.getMessage());
        assertEquals(1, order.items().size());
        verify(orderRepositoryPort).findById(orderId);
        verify(orderRepositoryPort, never()).save(any());
    }
}
