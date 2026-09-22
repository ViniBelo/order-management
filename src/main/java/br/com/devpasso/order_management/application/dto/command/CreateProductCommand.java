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
        validateName(name);
        validateDescription(description);
        validatePrice(price);
        validateStockQuantity(stockQuantity);
    }

    private void validateName(String name) {
        validateNotNull("Name", name);
        if (name.length() < 3) {
            throw new IllegalArgumentException("Name must have at least 3 characters");
        }
    }

    private void validateDescription(String description) {
        validateNotNull("Description", description);
    }

    private void validatePrice(BigDecimal price) {
        validateNotNull("Price", price);
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero");
        }
    }

    private void validateStockQuantity(Integer stockQuantity) {
        validateNotNull("StockQuantity", stockQuantity);
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("StockQuantity must be greater than or equal to zero");
        }
    }

    private <T> void validateNotNull(String fieldName, T field) {
        if (field == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
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
