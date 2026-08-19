package br.com.devpasso.order_management.application.dto.response;

import br.com.devpasso.order_management.domain.model.Product;

import java.math.BigDecimal;

public record CreateProductResponse(
        String id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
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
