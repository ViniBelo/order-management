package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.request.orders.CreateOrderRequest;
import br.com.devpasso.order_management.api.dto.response.orders.OrderResponse;
import br.com.devpasso.order_management.api.mapper.orders.request.CreateOrderRequestMapper;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderResponseMapper;
import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.usecase.orders.CreateOrderUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateOrdersControllerTest {

    @Mock
    private CreateOrderUseCase createOrderUseCase;

    @Mock
    private CreateOrderRequestMapper createOrderRequestMapper;

    @Mock
    private OrderResponseMapper orderResponseMapper;

    @InjectMocks
    private CreateOrdersController createOrdersController;

    @Test
    @DisplayName("Should create order and return 201 Created status with location header and order response")
    void createOrder_ShouldReturnCreatedWithLocationAndOrderResponse() {
        // Given
        UUID customerId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        CreateOrderRequest request = new CreateOrderRequest(customerId);
        CreateOrderCommand command = new CreateOrderCommand(customerId);

        OrderResult orderResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        OrderResponse expectedResponse = new OrderResponse(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        when(createOrderRequestMapper.toCommand(request)).thenReturn(command);
        when(createOrderUseCase.execute(command)).thenReturn(orderResult);
        when(orderResponseMapper.toResponse(orderResult)).thenReturn(expectedResponse);

        // When
        ResponseEntity<OrderResponse> response = createOrdersController.createOrder(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/v1/orders/" + orderId), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().id());
        assertEquals(customerId, response.getBody().customerId());
        assertEquals("DRAFT", response.getBody().status());
        assertTrue(response.getBody().items().isEmpty());
        assertEquals(BigDecimal.ZERO, response.getBody().total());
        assertEquals(createdAt, response.getBody().createdAt());

        verify(createOrderRequestMapper).toCommand(request);
        verify(createOrderUseCase).execute(command);
        verify(orderResponseMapper).toResponse(orderResult);
    }
}
