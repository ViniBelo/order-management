package br.com.devpasso.order_management.application.dto.result.orders;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResult(
        UUID id,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) { }
