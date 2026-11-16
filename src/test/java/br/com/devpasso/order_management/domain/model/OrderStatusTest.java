package br.com.devpasso.order_management.domain.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderStatusTest {

    @Test
    void shouldAllowDraftTransitionToConfirmed() {
        // Given
        OrderStatus status = OrderStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CONFIRMED);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldAllowDraftTransitionToCancelled() {
        // Given
        OrderStatus status = OrderStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CANCELLED);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotAllowDraftTransitionToDraft() {
        // Given
        OrderStatus status = OrderStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DRAFT);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDraftTransitionToInDelivery() {
        // Given
        OrderStatus status = OrderStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(OrderStatus.IN_DELIVERY);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDraftTransitionToDelivered() {
        // Given
        OrderStatus status = OrderStatus.DRAFT;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DELIVERED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldAllowConfirmedTransitionToInDelivery() {
        // Given
        OrderStatus status = OrderStatus.CONFIRMED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.IN_DELIVERY);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldAllowConfirmedTransitionToCancelled() {
        // Given
        OrderStatus status = OrderStatus.CONFIRMED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CANCELLED);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotAllowConfirmedTransitionToConfirmed() {
        // Given
        OrderStatus status = OrderStatus.CONFIRMED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CONFIRMED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowConfirmedTransitionToDelivered() {
        // Given
        OrderStatus status = OrderStatus.CONFIRMED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DELIVERED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowConfirmedTransitionToDraft() {
        // Given
        OrderStatus status = OrderStatus.CONFIRMED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DRAFT);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldAllowInDeliveryTransitionToDelivered() {
        // Given
        OrderStatus status = OrderStatus.IN_DELIVERY;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DELIVERED);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldAllowInDeliveryTransitionToCancelled() {
        // Given
        OrderStatus status = OrderStatus.IN_DELIVERY;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CANCELLED);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldNotAllowInDeliveryTransitionToInDelivery() {
        // Given
        OrderStatus status = OrderStatus.IN_DELIVERY;

        // When
        boolean result = status.canTransitionTo(OrderStatus.IN_DELIVERY);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowInDeliveryTransitionToDraft() {
        // Given
        OrderStatus status = OrderStatus.IN_DELIVERY;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DRAFT);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowInDeliveryTransitionToConfirmed() {
        // Given
        OrderStatus status = OrderStatus.IN_DELIVERY;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CONFIRMED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDeliveredTransitionToDelivered() {
        // Given
        OrderStatus status = OrderStatus.DELIVERED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DELIVERED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDeliveredTransitionToDraft() {
        // Given
        OrderStatus status = OrderStatus.DELIVERED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DRAFT);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDeliveredTransitionToConfirmed() {
        // Given
        OrderStatus status = OrderStatus.DELIVERED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CONFIRMED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDeliveredTransitionToInDelivery() {
        // Given
        OrderStatus status = OrderStatus.DELIVERED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.IN_DELIVERY);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowDeliveredTransitionToCancelled() {
        // Given
        OrderStatus status = OrderStatus.DELIVERED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CANCELLED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowCancelledTransitionToCancelled() {
        // Given
        OrderStatus status = OrderStatus.CANCELLED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CANCELLED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowCancelledTransitionToDraft() {
        // Given
        OrderStatus status = OrderStatus.CANCELLED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DRAFT);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowCancelledTransitionToConfirmed() {
        // Given
        OrderStatus status = OrderStatus.CANCELLED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.CONFIRMED);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowCancelledTransitionToInDelivery() {
        // Given
        OrderStatus status = OrderStatus.CANCELLED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.IN_DELIVERY);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldNotAllowCancelledTransitionToDelivered() {
        // Given
        OrderStatus status = OrderStatus.CANCELLED;

        // When
        boolean result = status.canTransitionTo(OrderStatus.DELIVERED);

        // Then
        assertFalse(result);
    }
}
