package br.com.devpasso.order_management.application.command.orders;

import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CreateOrderCommandTest {

    @Test
    @DisplayName("Should create command with valid customerId")
    void shouldCreateCommandWithValidCustomerId() {
        // Given
        UUID customerId = UUID.randomUUID();

        // When
        CreateOrderCommand command = new CreateOrderCommand(customerId);

        // Then
        assertNotNull(command);
        assertEquals(customerId, command.customerId());
    }

    @Test
    @DisplayName("Should convert command to Order domain model with default initial state")
    void shouldConvertToDomainModel() {
        // Given
        UUID customerId = UUID.randomUUID();
        CreateOrderCommand command = new CreateOrderCommand(customerId);

        // When
        Order order = command.toDomainModel();

        // Then
        assertNotNull(order);
        assertNotNull(order.id());
        assertEquals(customerId, order.customerId());
        assertEquals(OrderStatus.DRAFT, order.status());
        assertNotNull(order.items());
        assertTrue(order.items().isEmpty());
        assertNotNull(order.createdAt());
    }
}
