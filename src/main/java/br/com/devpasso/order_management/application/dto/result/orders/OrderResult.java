package br.com.devpasso.order_management.application.dto.result.orders;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResult(
        UUID orderId,
        UUID customerId,
        String orderStatus,
        List<OrderItemResult> items,
        BigDecimal orderTotal,
        Instant createdAt
) { }
