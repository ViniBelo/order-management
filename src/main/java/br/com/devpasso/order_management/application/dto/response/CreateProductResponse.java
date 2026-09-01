package br.com.devpasso.order_management.application.dto.response;

import br.com.devpasso.order_management.domain.model.Product;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record CreateProductResponse(
        @Schema(description = "Product unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        String id,
        @Schema(description = "Product name", example = "Smartphone XYZ")
        String name,
        @Schema(description = "Product description", example = "High-end smartphone with OLED display")
        String description,
        @Schema(description = "Product price", example = "999.99")
        BigDecimal price,
        @Schema(description = "Product stock quantity", example = "50")
        Integer stockQuantity,
        @Schema(description = "Creation timestamp", example = "2026-08-31T12:00:00Z")
        String createdAt
) {
    public static CreateProductResponse build(Product product) {
        return new CreateProductResponse(
                product.getId()
                        .toString(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt()
                        .toString()
        );
    }
}
