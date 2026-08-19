package br.com.devpasso.order_management.application.dto.command;

import br.com.devpasso.order_management.domain.model.Product;

import java.math.BigDecimal;

public record CreateProductCommand(
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) {
    public CreateProductCommand {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        if (description == null) {
            throw new IllegalArgumentException("Description cannot be null");
        }
        if (price == null) {
            throw new IllegalArgumentException("Price cannot be null");
        }
        if (stockQuantity == null) {
            throw new IllegalArgumentException("Stock quantity cannot be null");
        }
    }

    public Product toDomainModel() {
        return new Product(null,
                name,
                description,
                price,
                stockQuantity,
                null);
    }
}
