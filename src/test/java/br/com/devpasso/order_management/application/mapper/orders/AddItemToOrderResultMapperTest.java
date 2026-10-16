package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.AddItemToOrderResult;
import br.com.devpasso.order_management.domain.model.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddItemToOrderResultMapperTest {

    private final AddItemToOrderResultMapper mapper = new AddItemToOrderResultMapper();

    @Test
    @DisplayName("Should map OrderItem to AddItemToOrderResult correctly")
    void shouldMapOrderItemToAddItemToOrderResult() {
        // Given
        UUID itemId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        String productName = "Wireless Mouse";
        BigDecimal unitPrice = new BigDecimal("75.50");
        Integer quantity = 2;

        OrderItem item = new OrderItem(itemId, productId, productName, unitPrice, quantity);

        // When
        AddItemToOrderResult result = mapper.map(item);

        // Then
        assertNotNull(result);
        assertEquals(itemId, result.productId());
        assertEquals(productName, result.productName());
        assertEquals(unitPrice, result.unitPrice());
        assertEquals(quantity, result.quantity());
        assertEquals(new BigDecimal("151.00"), result.subtotal());
    }
}
