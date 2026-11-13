package br.com.devpasso.order_management.domain.model;

import br.com.devpasso.order_management.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderItemTest {

    @Test
    public void shouldCreateOrderItem() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                1,
                Instant.now()
        );

        // When
        OrderItem orderItem = new OrderItem(
                id,
                product,
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
        );

        // Then
        assertEquals(id, orderItem.id());
        assertEquals(product.getId(), orderItem.product().getId());
        assertEquals(product.getName(), orderItem.productName());
        assertEquals(product.getPrice(), orderItem.unitPrice());
        assertEquals(product.getStockQuantity(), orderItem.quantity());
    }

    @Test
    public void subtotal_shouldReturnUnitPriceTimesQuantity() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("10.00"),
                2,
                Instant.now()
        );

        // When
        OrderItem orderItem = new OrderItem(
                id,
                product,
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
        );

        // Then
        assertEquals(new BigDecimal("20.00"), orderItem.subtotal());
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithNullUnitPrice() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                null,
                1,
                Instant.now()
        );

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                id,
                product,
                product.getName(),
                null,
                product.getStockQuantity()
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithUnitPriceZeroed() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                BigDecimal.ZERO,
                1,
                Instant.now()
        );

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                id,
                product,
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithUnitPriceSmallerThanZero() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("-150.00"),
                1,
                Instant.now()
        );

        // When && Then
        assertThrows(DomainException.class, () -> {
            new OrderItem(
                id,
                product,
                product.getName(),
                product.getPrice(),
                product.getStockQuantity()
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithNullQuantity() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                1,
                Instant.now()
        );

        // When && Then
        assertThrows(DomainException.class, () -> {
            new OrderItem(
                    id,
                    product,
                    product.getName(),
                    product.getPrice(),
                    null
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithQuantityZero() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                0,
                Instant.now()
        );

        // When && Then
        assertThrows(DomainException.class, () -> {
            new OrderItem(
                    id,
                    product,
                    product.getName(),
                    product.getPrice(),
                    product.getStockQuantity()
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithQuantitySmallerThanZero() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                -1,
                Instant.now()
        );

        // When && Then
        assertThrows(DomainException.class, () -> {
            new OrderItem(
                    id,
                    product,
                    product.getName(),
                    product.getPrice(),
                    product.getStockQuantity()
            );
        });
    }
}
