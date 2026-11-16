package br.com.devpasso.order_management.api.dto.response.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID customerId,
        String status,
        List<OrderItemResponse> items,
        BigDecimal total,
        Instant createdAt
) { }
