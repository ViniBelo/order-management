package br.com.devpasso.order_management.api.mapper.orders.response;

import br.com.devpasso.order_management.api.dto.response.orders.OrderItemResponse;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class OrderItemResponseMapperTest {

    private final OrderItemResponseMapper mapper = new OrderItemResponseMapper();

    @Test
    @DisplayName("Should map OrderItemResult to OrderItemResponse successfully")
    void shouldMapOrderItemResultToResponse() {
        // Given
        UUID itemId = UUID.randomUUID();
        String productName = "Mechanical Keyboard";
        BigDecimal unitPrice = new BigDecimal("120.00");
        Integer quantity = 2;
        BigDecimal subtotal = new BigDecimal("240.00");

        OrderItemResult result = new OrderItemResult(
                itemId,
                productName,
                unitPrice,
                quantity,
                subtotal
        );

        // When
        OrderItemResponse response = mapper.toResponse(result);

        // Then
        assertNotNull(response);
        assertEquals(itemId, response.id());
        assertEquals(productName, response.productName());
        assertEquals(unitPrice, response.unitPrice());
        assertEquals(quantity, response.quantity());
        assertEquals(subtotal, response.subtotal());
    }
}
