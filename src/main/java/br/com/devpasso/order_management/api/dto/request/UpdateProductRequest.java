package br.com.devpasso.order_management.api.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record UpdateProductRequest(
        @Schema(description = "Product name", example = "Smartphone XYZ Pro")
        @NotBlank(message = "Name is required")
        @Size(min = 3, max = 255, message = "Name must be between 3 and 255 characters")
        String name,

        @Schema(description = "Product description", example = "Updated product description")
        String description,

        @Schema(description = "Product price", example = "1099.99")
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than zero")
        BigDecimal price
) { }
