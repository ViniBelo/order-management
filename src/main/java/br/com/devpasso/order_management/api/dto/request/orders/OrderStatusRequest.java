package br.com.devpasso.order_management.api.dto.request.orders;

import jakarta.validation.constraints.NotNull;

@NotNull
public enum OrderStatusRequest {
    DRAFT,
    CONFIRMED,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}
