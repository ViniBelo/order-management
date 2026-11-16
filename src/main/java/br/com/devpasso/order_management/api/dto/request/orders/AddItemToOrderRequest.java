package br.com.devpasso.order_management.api.dto.request.orders;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record AddItemToOrderRequest(
        @NotNull
        UUID productId,

        @NotNull
        @Positive
        Integer quantity
) { }
