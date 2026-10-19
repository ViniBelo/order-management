package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.domain.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemResultMapperTest {

    private final OrderItemResultMapper mapper = new OrderItemResultMapper();

    @Test
    @DisplayName("Should map OrderItem to OrderItemResult correctly")
    void shouldMapOrderItemToOrderItemResult() {
        // Given
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Mechanical Keyboard";
        BigDecimal unitPrice = new BigDecimal("120.00");
        Integer quantity = 1;

        OrderItem item = new OrderItem(itemId, productId, productName, unitPrice, quantity);

        // When
        OrderItemResult result = mapper.map(item);

        // Then
        assertNotNull(result);
        assertEquals(itemId, result.id());
        assertEquals(productName, result.productName());
        assertEquals(unitPrice, result.unitPrice());
        assertEquals(quantity, result.quantity());
    }
}
