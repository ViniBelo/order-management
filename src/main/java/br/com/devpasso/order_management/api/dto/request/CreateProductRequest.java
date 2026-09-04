package br.com.devpasso.order_management.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @Schema(description = "Product name", example = "Smartphone XYZ")
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        @Schema(description = "Product description", example = "High-end smartphone with OLED display")
        String description,

        @Schema(description = "Product price", example = "999.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price,

        @Schema(description = "Product initial stock quantity", example = "50")
        @NotNull(message = "Stock quantity is required")
        @PositiveOrZero(message = "Stock quantity must be greater than or equal to zero")
        Integer stockQuantity
) { }
