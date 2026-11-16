package br.com.devpasso.order_management.application.command.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.domain.exception.DomainException;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
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
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                3,
                Instant.now()
        );

        UUID orderId = UUID.randomUUID();
        Integer quantity = 3;
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, product.getId(), quantity);

        // When
        OrderItem orderItem = command.toDomainModel(product);

        // Then
        assertNotNull(orderItem);
        assertNull(orderItem.id());
        assertEquals(product.getId(), orderItem.product().getId());
        assertEquals(product.getName(), orderItem.productName());
        assertEquals(product.getPrice(), orderItem.unitPrice());
        assertEquals(quantity, orderItem.quantity());
        assertEquals(new BigDecimal("450.00"), orderItem.subtotal());
    }

    @Test
    @DisplayName("Should throw DomainException when converting to OrderItem with invalid quantity")
    void shouldThrowDomainExceptionWhenConvertingWithInvalidQuantity() {
        // Given
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                1,
                Instant.now()
        );
        UUID orderId = UUID.randomUUID();
        AddItemToOrderCommand commandWithZeroQuantity = new AddItemToOrderCommand(orderId, product.getId(), 0);
        AddItemToOrderCommand commandWithNullQuantity = new AddItemToOrderCommand(orderId, product.getId(), null);

        // When & Then
        assertThrows(DomainException.class, () ->
                commandWithZeroQuantity.toDomainModel(product)
        );
        assertThrows(DomainException.class, () ->
                commandWithNullQuantity.toDomainModel(product)
        );
    }

    @Test
    @DisplayName("Should throw DomainException when converting to OrderItem with invalid unit price")
    void shouldThrowDomainExceptionWhenConvertingWithInvalidUnitPrice() {
        // Given
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("0.0"),
                1,
                Instant.now()
        );
        UUID orderId = UUID.randomUUID();
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, product.getId(), 1);

        // When & Then
        assertThrows(DomainException.class, () ->
                command.toDomainModel(product)
        );
    }

    @Test
    @DisplayName("Should throw DomainException when converting to OrderItem with null unit price")
    void shouldThrowDomainExceptionWhenConvertingWithNullUnitPrice() {
        // Given
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                null,
                1,
                Instant.now()
        );
        UUID orderId = UUID.randomUUID();
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, product.getId(), 1);

        // When & Then
        assertThrows(DomainException.class, () ->
                command.toDomainModel(product)
        );
    }
}
