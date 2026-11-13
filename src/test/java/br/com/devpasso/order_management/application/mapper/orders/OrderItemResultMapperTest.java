package br.com.devpasso.order_management.application.mapper.orders;

import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderItemResultMapperTest {

    private final OrderItemResultMapper mapper = new OrderItemResultMapper();

    @Test
    @DisplayName("Should map OrderItem to OrderItemResult correctly")
    void shouldMapOrderItemToOrderItemResult() {
        // Given
        Product product = new Product(
                UUID.randomUUID(),
                "Keyboard",
                "Description",
                new BigDecimal("150.00"),
                1,
                Instant.now()
        );
        UUID itemId = UUID.randomUUID();
        Integer quantity = 1;

        OrderItem item = new OrderItem(itemId, product, product.getName(), product.getPrice(), quantity);

        // When
        OrderItemResult result = mapper.map(item);

        // Then
        assertNotNull(result);
        assertEquals(itemId, result.id());
        assertEquals(product.getName(), result.productName());
        assertEquals(product.getPrice(), result.unitPrice());
        assertEquals(quantity, result.quantity());
    }
}
