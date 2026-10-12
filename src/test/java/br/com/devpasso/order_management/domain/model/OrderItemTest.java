package br.com.devpasso.order_management.domain.model;

import br.com.devpasso.order_management.domain.exception.DomainException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderItemTest {

    @Test
    public void shouldCreateOrderItem() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 1;

        // When
        OrderItem orderItem = new OrderItem(
                id,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // Then
        assertEquals(id, orderItem.id());
        assertEquals(itemId, orderItem.productId());
        assertEquals(productName, orderItem.productName());
        assertEquals(unitPrice, orderItem.unitPrice());
        assertEquals(quantity, orderItem.quantity());
    }

    @Test
    public void subtotal_shouldReturnUnitPriceTimesQuantity() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("10.00");
        Integer quantity = 2;

        // When
        OrderItem orderItem = new OrderItem(
                id,
                itemId,
                productName,
                unitPrice,
                quantity
        );

        // Then
        assertEquals(new BigDecimal("20.00"), orderItem.subtotal());
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithNullUnitPrice() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        Integer quantity = 1;

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                id,
                itemId,
                productName,
                null,
                quantity
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithUnitPriceZeroed() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = BigDecimal.ZERO;
        Integer quantity = 1;

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                id,
                itemId,
                productName,
                unitPrice,
                quantity
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithUnitPriceSmallerThanZero() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("-1.00");
        Integer quantity = 1;

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                id,
                itemId,
                productName,
                unitPrice,
                quantity
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithNullQuantity() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("1.00");

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                    id,
                    itemId,
                    productName,
                    unitPrice,
                    null
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithQuantityZero() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("1.00");
        Integer quantity = 0;

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                    id,
                    itemId,
                    productName,
                    unitPrice,
                    quantity
            );
        });
    }

    @Test
    public void shouldThrowDomainExceptionWhenTryingToCreateWithQuantitySmallerThanZero() {
        // Given
        UUID id = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        String productName = "Product 1";
        BigDecimal unitPrice = new BigDecimal("1.00");
        Integer quantity = -1;

        // When && Then
        assertThrows(DomainException.class, () -> {
            OrderItem orderItem = new OrderItem(
                    id,
                    itemId,
                    productName,
                    unitPrice,
                    quantity
            );
        });
    }
}
