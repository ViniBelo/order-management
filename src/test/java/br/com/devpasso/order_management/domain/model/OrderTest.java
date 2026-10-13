package br.com.devpasso.order_management.domain.model;

import br.com.devpasso.order_management.domain.exception.InvalidOrderStateException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {

    @Test
    public void addItem_shouldAddItem() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 1;

        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // When
        order.addItem(orderItem);

        // Then
        assertEquals(orderItem, order.items().getFirst());
        assertEquals(1, order.items().size());
    }

    @Test
    public void addItem_shouldThrowInvalidOrderStateExceptionWhenAddingItemToConfirmedOrder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);
        order.transitionTo(OrderStatus.CONFIRMED);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 1;

        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // When && Then
        assertThrows(InvalidOrderStateException.class, () -> order.addItem(orderItem));
    }

    @Test
    public void addItem_shouldThrowInvalidOrderStateExceptionWhenAddingItemToInDeliveryOrder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.IN_DELIVERY);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 1;

        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // When && Then
        assertThrows(InvalidOrderStateException.class, () -> order.addItem(orderItem));
    }

    @Test
    public void addItem_shouldThrowInvalidOrderStateExceptionWhenAddingItemToDeliveredOrder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.IN_DELIVERY);
        order.transitionTo(OrderStatus.DELIVERED);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 1;

        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // When && Then
        assertThrows(InvalidOrderStateException.class, () -> order.addItem(orderItem));
    }

    @Test
    public void addItem_shouldThrowInvalidOrderStateExceptionWhenAddingItemToCancelledOrder() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);
        order.transitionTo(OrderStatus.CANCELLED);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 1;

        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // When && Then
        assertThrows(InvalidOrderStateException.class, () -> order.addItem(orderItem));
    }

    @Test
    public void transitionTo_shouldUpdateOrderStatus() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);

        // When
        order.transitionTo(OrderStatus.CONFIRMED);

        // Then
        assertEquals(OrderStatus.CONFIRMED, order.status());
    }

    @Test
    public void transitionTo_shouldThrowInvalidOrderStateExceptionWhenInvalidNextOrderStatus() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();

        Order order = new Order(id, customerId);

        // When && Then
        assertThrows(InvalidOrderStateException.class, () -> order.transitionTo(OrderStatus.IN_DELIVERY));
    }
}
