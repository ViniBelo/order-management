package br.com.devpasso.order_management.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductStockRequest(
        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity must be greater than or equal to zero")
        Integer stockQuantity
) { }
