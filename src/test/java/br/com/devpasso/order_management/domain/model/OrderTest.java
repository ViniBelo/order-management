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
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);

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
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);
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
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);
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
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);
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
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);
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
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);

        // When
        order.transitionTo(OrderStatus.CONFIRMED);

        // Then
        assertEquals(OrderStatus.CONFIRMED, order.status());
    }

    @Test
    public void transitionTo_shouldThrowInvalidOrderStateExceptionWhenInvalidNextOrderStatus() {
        // Given
        UUID customerId = UUID.randomUUID();

        Order order = new Order(customerId);

        // When && Then
        assertThrows(InvalidOrderStateException.class, () -> order.transitionTo(OrderStatus.IN_DELIVERY));
    }

    @Test
    public void total_shouldSumOrderItemsSubtotals() {
        // Given
        UUID orderItemId1 = UUID.randomUUID();
        UUID itemId1 = UUID.randomUUID();
        String productName1 = "Product 1";
        BigDecimal unitPrice1 = new BigDecimal("10.00");
        Integer quantity1 = 2;

        OrderItem orderItem1 = new OrderItem(
                orderItemId1,
                itemId1,
                productName1,
                unitPrice1,
                quantity1
        );

        UUID orderItemId2 = UUID.randomUUID();
        UUID itemId2 = UUID.randomUUID();
        String productName2 = "Product 2";
        BigDecimal unitPrice2 = new BigDecimal("20.00");
        Integer quantity2 = 3;

        OrderItem orderItem2 = new OrderItem(
                orderItemId2,
                itemId2,
                productName2,
                unitPrice2,
                quantity2
        );

        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);
        order.addItem(orderItem1);
        order.addItem(orderItem2);

        // When
        BigDecimal total = order.total();

        // Then
        assertEquals(new BigDecimal("80.00"), total);
    }

    @Test
    public void removeItem_shouldRemoveItem() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );
        order.addItem(orderItem);

        // When
        order.removeItem(orderItemId);

        // Then
        assertEquals(0, order.items().size());
    }

    @Test
    public void removeItem_shouldOnlyRemoveSpecifiedItemWhenMultipleItemsExist() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId1 = UUID.randomUUID();
        UUID itemId1 = UUID.randomUUID();
        OrderItem orderItem1 = new OrderItem(
                orderItemId1,
                itemId1,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );

        UUID orderItemId2 = UUID.randomUUID();
        UUID itemId2 = UUID.randomUUID();
        OrderItem orderItem2 = new OrderItem(
                orderItemId2,
                itemId2,
                "Product 2",
                new BigDecimal("20.00"),
                2
        );

        order.addItem(orderItem1);
        order.addItem(orderItem2);

        // When
        order.removeItem(orderItemId1);

        // Then
        assertEquals(1, order.items().size());
        assertEquals(orderItem2, order.items().getFirst());
    }

    @Test
    public void removeItem_shouldDoNothingWhenItemNotFound() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );
        order.addItem(orderItem);

        // When
        order.removeItem(UUID.randomUUID());

        // Then
        assertEquals(1, order.items().size());
        assertEquals(orderItem, order.items().getFirst());
    }

    @Test
    public void removeItem_shouldThrowInvalidOrderStateExceptionWhenRemovingItemFromConfirmedOrder() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );
        order.addItem(orderItem);
        order.transitionTo(OrderStatus.CONFIRMED);

        // When & Then
        assertThrows(InvalidOrderStateException.class, () -> order.removeItem(orderItemId));
    }

    @Test
    public void removeItem_shouldThrowInvalidOrderStateExceptionWhenRemovingItemFromInDeliveryOrder() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );
        order.addItem(orderItem);
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.IN_DELIVERY);

        // When & Then
        assertThrows(InvalidOrderStateException.class, () -> order.removeItem(orderItemId));
    }

    @Test
    public void removeItem_shouldThrowInvalidOrderStateExceptionWhenRemovingItemFromDeliveredOrder() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );
        order.addItem(orderItem);
        order.transitionTo(OrderStatus.CONFIRMED);
        order.transitionTo(OrderStatus.IN_DELIVERY);
        order.transitionTo(OrderStatus.DELIVERED);

        // When & Then
        assertThrows(InvalidOrderStateException.class, () -> order.removeItem(orderItemId));
    }

    @Test
    public void removeItem_shouldThrowInvalidOrderStateExceptionWhenRemovingItemFromCancelledOrder() {
        // Given
        UUID customerId = UUID.randomUUID();
        Order order = new Order(customerId);

        UUID orderItemId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        OrderItem orderItem = new OrderItem(
                orderItemId,
                itemId,
                "Product 1",
                new BigDecimal("10.00"),
                1
        );
        order.addItem(orderItem);
        order.transitionTo(OrderStatus.CANCELLED);

        // When & Then
        assertThrows(InvalidOrderStateException.class, () -> order.removeItem(orderItemId));
    }
}
