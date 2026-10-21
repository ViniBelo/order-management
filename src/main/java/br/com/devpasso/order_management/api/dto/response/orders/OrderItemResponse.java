package br.com.devpasso.order_management.api.dto.response.orders;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        String productName,
        BigDecimal unitPrice,
        Integer quantity,
        BigDecimal subtotal
) { }
