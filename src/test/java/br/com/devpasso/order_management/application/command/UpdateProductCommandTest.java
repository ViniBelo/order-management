package br.com.devpasso.order_management.application.command;

import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UpdateProductCommandTest {
    @Test
    @DisplayName("Should create command with valid values")
    void shouldCreateCommandWithValidValues() {
        // Given
        String name = "Product name";
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");

        // When
        UpdateProductCommand command = new UpdateProductCommand(
                name,
                description,
                price
        );

        // Then
        assertEquals(name, command.name());
        assertEquals(description, command.description());
        assertEquals(price, command.price());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given name is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenNameIsNull() {
        // Given
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new UpdateProductCommand(
                null,
                description,
                price
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given name has less than 3 characters")
    void shouldThrowIllegalArgumentExceptionWhenGivenNameHasLessThanThreeCharacters (){
        // Given
        String name = "";
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new UpdateProductCommand(
                name,
                description,
                price
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given description is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenDescriptionIsNull() {
        // Given
        String name = "Product name";
        BigDecimal price = new BigDecimal("49.99");

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new UpdateProductCommand(
                name,
                null,
                price
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given price is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenPriceIsNull() {
        // Given
        String name = "Product name";
        String description = "Product description";

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new UpdateProductCommand(
                name,
                description,
                null
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given price is smaller or equal to zero")
    void shouldThrowIllegalArgumentExceptionWhenGivenPriceIsSmallerOrEqualToZero () {
        // Given
        String name = "Product name";
        String description = "Product description";
        BigDecimal price = new BigDecimal("0");

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new UpdateProductCommand(
                name,
                description,
                price
        ));
    }
}
