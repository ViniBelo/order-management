package br.com.devpasso.order_management.domain.model;

import br.com.devpasso.order_management.domain.exception.DomainException;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItem(
        UUID id,
        UUID productId,
        String productName,
        BigDecimal unitPrice,
        Integer quantity
) {
    public OrderItem {
        if (quantity == null || quantity <= 0) {
            throw new DomainException("Quantity must be greater than zero");
        }

        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new DomainException("Unit price must be greater than zero");
        }
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
