package br.com.devpasso.order_management.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @NotBlank
        @Size(min = 3, max = 255)
        String name,

        String description,

        @NotNull
        @DecimalMin("0.01")
        BigDecimal price
        ) { }
