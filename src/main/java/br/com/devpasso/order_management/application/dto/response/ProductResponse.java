package br.com.devpasso.order_management.application.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductResponse(
        @Schema(description = "Product unique identifier", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,
        @Schema(description = "Product name", example = "Smartphone XYZ")
        String name,
        @Schema(description = "Product description", example = "High-end smartphone with OLED display")
        String description,
        @Schema(description = "Product price", example = "999.99")
        BigDecimal price,
        @Schema(description = "Product stock quantity", example = "50")
        Integer stockQuantity
) { }
