package br.com.devpasso.order_management.application.dto.result;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResult(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        Integer stockQuantity
) { }
