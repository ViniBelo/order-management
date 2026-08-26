package br.com.devpasso.order_management.application.dto.command;

import br.com.devpasso.order_management.domain.model.Product;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String name,
        String description,
        BigDecimal price
) {
    public Product toDomainModel() {
        return new Product(null,
                name,
                description,
                price,
                null,
                null);
    }
}
