package br.com.devpasso.order_management.application.command;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CreateProductCommandTest {
    @Test
    @DisplayName("Should create command with valid values")
    void shouldCreateCommandWithValidValues() {
        // Given
        String name = "Product name";
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");
        Integer stockQuantity = 10;

        // When
        CreateProductCommand command = new CreateProductCommand(
                name,
                description,
                price,
                stockQuantity
        );

        // Then
        assertEquals(name, command.name());
        assertEquals(description, command.description());
        assertEquals(price, command.price());
        assertEquals(stockQuantity, command.stockQuantity());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given name is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenNameIsNull() {
        // Given
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");
        Integer stockQuantity = 10;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                null,
                description,
                price,
                stockQuantity
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given name has less than 3 characters")
    void shouldThrowIllegalArgumentExceptionWhenGivenNameHasLessThanThreeCharacters (){
        // Given
        String name = "";
        String description = "Product description";
        BigDecimal price = new BigDecimal("49.99");
        Integer stockQuantity = 10;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                name,
                description,
                price,
                stockQuantity
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given description is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenDescriptionIsNull() {
        // Given
        String name = "Product name";
        BigDecimal price = new BigDecimal("49.99");
        Integer stockQuantity = 10;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                name,
                null,
                price,
                stockQuantity
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given price is null")
    void shouldThrowIllegalArgumentExceptionWhenGivenPriceIsNull() {
        // Given
        String name = "Product name";
        String description = "Product description";
        Integer stockQuantity = 10;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                name,
                description,
                null,
                stockQuantity
        ));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when given price is smaller or equal to zero")
    void shouldThrowIllegalArgumentExceptionWhenGivenPriceIsSmallerOrEqualToZero () {
        // Given
        String name = "Product name";
        String description = "Product description";
        BigDecimal price = new BigDecimal("0");
        Integer stockQuantity = 10;

        // When && Then
        assertThrows(IllegalArgumentException.class, () -> new CreateProductCommand(
                name,
                description,
                price,
                stockQuantity
        ));
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
