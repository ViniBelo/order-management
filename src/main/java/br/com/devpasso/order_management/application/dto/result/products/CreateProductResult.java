package br.com.devpasso.order_management.application.dto.result.products;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record CreateProductResult(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity,
        Instant createdAt
) { }
