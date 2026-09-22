package br.com.devpasso.order_management.application.command;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateProductStockCommandTest {
    @Test
    @DisplayName("Should create command with valid values")
    void shouldCreateCommandWithValidValues() {
        // Given
        Integer stockQuantity = 10;

        // When
        UpdateProductStockCommand command = new UpdateProductStockCommand(stockQuantity);

        // Then
        assertEquals(stockQuantity, command.stockQuantity());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given stock quantity is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenStockQuantityIsNull() {
        // Given
        String name = "Product name";
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                name,
                description,
                price,
                null
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given stockQuantity is negative")
    void shouldThrowIllegalArgumentExceptionWhenGivenStockQuantityIsNegative () {
        // Given
        String name = "Product name";
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");
        Integer stockQuantity = -1;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                name,
                description,
                price,
                stockQuantity
        ));
    }
}
