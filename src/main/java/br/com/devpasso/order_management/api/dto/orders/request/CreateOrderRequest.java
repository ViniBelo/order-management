package br.com.devpasso.order_management.api.dto.orders.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateOrderRequest(
        @NotNull
        UUID customerId
) { }
