package br.com.devpasso.order_management.api.mapper.orders.request;

import br.com.devpasso.order_management.api.dto.request.orders.CreateOrderRequest;
import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CreateOrderRequestMapperTest {

    private final CreateOrderRequestMapper mapper = new CreateOrderRequestMapper();

    @Test
    @DisplayName("Should map CreateOrderRequest to CreateOrderCommand successfully")
    void shouldMapCreateOrderRequestToCommand() {
        // Given
        UUID customerId = UUID.randomUUID();
        CreateOrderRequest request = new CreateOrderRequest(customerId);

        // When
        CreateOrderCommand command = mapper.toCommand(request);

        // Then
        assertNotNull(command);
        assertEquals(customerId, command.customerId());
    }
}
