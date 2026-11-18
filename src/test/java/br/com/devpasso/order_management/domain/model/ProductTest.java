package br.com.devpasso.order_management.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProductTest {

    @Test
    @DisplayName("Should update stock quantity when value is valid positive integer")
    void changeStockQuantity_ShouldUpdateStockQuantity_WhenPositive() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                10,
                Instant.now()
        );

        product.changeStockQuantity(25);

        assertEquals(25, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should update stock quantity when value is zero")
    void changeStockQuantity_ShouldUpdateStockQuantity_WhenZero() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                10,
                Instant.now()
        );

        product.changeStockQuantity(0);

        assertEquals(0, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when stock quantity is negative")
    void changeStockQuantity_ShouldThrowException_WhenNegative() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                10,
                Instant.now()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.changeStockQuantity(-1)
        );

        assertEquals("Stock quantity cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when stock quantity is null")
    void changeStockQuantity_ShouldThrowException_WhenNull() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                10,
                Instant.now()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.changeStockQuantity(null)
        );

        assertEquals("Stock quantity cannot be negative", exception.getMessage());
    }

    @Test
    @DisplayName("Should decrease stock quantity by the requested amount")
    void decreaseStockQuantity_ShouldReduceStock() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                10,
                Instant.now()
        );

        product.decreaseStockQuantity(4);

        assertEquals(6, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should reject decreasing stock below zero")
    void decreaseStockQuantity_ShouldThrowException_WhenInsufficientStock() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                3,
                Instant.now()
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> product.decreaseStockQuantity(4)
        );

        assertEquals("Not enough stock quantity", exception.getMessage());
        assertEquals(3, product.getStockQuantity());
    }

    @Test
    @DisplayName("Should increase stock quantity by the requested amount")
    void increaseStockQuantity_ShouldIncreaseStock() {
        Product product = new Product(
                UUID.randomUUID(),
                "Product A",
                "Description A",
                new BigDecimal("19.99"),
                6,
                Instant.now()
        );

        product.increaseStockQuantity(4);

        assertEquals(10, product.getStockQuantity());
    }
}
