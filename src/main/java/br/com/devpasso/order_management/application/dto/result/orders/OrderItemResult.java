package br.com.devpasso.order_management.application.dto.result.orders;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResult(
        UUID productId,
        String productName,
        BigDecimal productPrice,
        Integer quantity
) { }
