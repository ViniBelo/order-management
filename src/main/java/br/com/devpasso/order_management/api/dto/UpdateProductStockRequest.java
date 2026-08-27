package br.com.devpasso.order_management.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record UpdateProductStockRequest(
        @NotNull
        @PositiveOrZero
        Integer stockQuantity
) { }
