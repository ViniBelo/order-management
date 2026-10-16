package br.com.devpasso.order_management.application.command.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.domain.exception.DomainException;
import br.com.devpasso.order_management.domain.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddItemToOrderCommandTest {

    @Test
    @DisplayName("Should create command with valid values")
    void shouldCreateCommandWithValidValues() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 2;

        // When
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, quantity);

        // Then
        assertNotNull(command);
        assertEquals(orderId, command.orderId());
        assertEquals(productId, command.productId());
        assertEquals(quantity, command.quantity());
    }

    @Test
    @DisplayName("Should convert command to OrderItem domain model")
    void shouldConvertToDomainModel() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 3;
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, quantity);
        String productName = "Keyboard";
        BigDecimal unitPrice = new BigDecimal("150.00");

        // When
        OrderItem orderItem = command.toDomainModel(productName, unitPrice);

        // Then
        assertNotNull(orderItem);
        assertNull(orderItem.id());
        assertEquals(productId, orderItem.productId());
        assertEquals(productName, orderItem.productName());
        assertEquals(unitPrice, orderItem.unitPrice());
        assertEquals(quantity, orderItem.quantity());
        assertEquals(new BigDecimal("450.00"), orderItem.subtotal());
    }

    @Test
    @DisplayName("Should throw DomainException when converting to OrderItem with invalid quantity")
    void shouldThrowDomainExceptionWhenConvertingWithInvalidQuantity() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderCommand commandWithZeroQuantity = new AddItemToOrderCommand(orderId, productId, 0);
        AddItemToOrderCommand commandWithNullQuantity = new AddItemToOrderCommand(orderId, productId, null);

        // When & Then
        assertThrows(DomainException.class, () ->
                commandWithZeroQuantity.toDomainModel("Keyboard", new BigDecimal("150.00"))
        );
        assertThrows(DomainException.class, () ->
                commandWithNullQuantity.toDomainModel("Keyboard", new BigDecimal("150.00"))
        );
    }

    @Test
    @DisplayName("Should throw DomainException when converting to OrderItem with invalid unit price")
    void shouldThrowDomainExceptionWhenConvertingWithInvalidUnitPrice() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, 1);

        // When & Then
        assertThrows(DomainException.class, () ->
                command.toDomainModel("Keyboard", BigDecimal.ZERO)
        );
        assertThrows(DomainException.class, () ->
                command.toDomainModel("Keyboard", null)
        );
    }
}
