package br.com.devpasso.order_management.api.mapper.orders.request;

import br.com.devpasso.order_management.api.dto.request.orders.AddItemToOrderRequest;
import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AddItemToOrderRequestMapperTest {

    private final AddItemToOrderRequestMapper mapper = new AddItemToOrderRequestMapper();

    @Test
    @DisplayName("Should map orderId and AddItemToOrderRequest to AddItemToOrderCommand successfully")
    void shouldMapAddItemToOrderRequestToCommand() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Integer quantity = 3;
        AddItemToOrderRequest request = new AddItemToOrderRequest(productId, quantity);

        // When
        AddItemToOrderCommand command = mapper.toCommand(orderId, request);

        // Then
        assertNotNull(command);
        assertEquals(orderId, command.orderId());
        assertEquals(productId, command.productId());
        assertEquals(quantity, command.quantity());
    }
}
