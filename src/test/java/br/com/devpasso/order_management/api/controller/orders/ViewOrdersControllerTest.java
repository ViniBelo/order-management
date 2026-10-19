package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.orders.response.OrderItemResponse;
import br.com.devpasso.order_management.api.dto.orders.response.OrderResponse;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderResponseMapper;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.usecase.orders.FindOrderByIdUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewOrdersControllerTest {

    @Mock
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @Mock
    private OrderResponseMapper orderResponseMapper;

    @InjectMocks
    private ViewOrdersController viewOrdersController;

    @Test
    @DisplayName("Should return order by ID and 200 OK status")
    void findOrderById_ShouldReturnOkWithOrderResponse() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        OrderItemResult itemResult = new OrderItemResult(
                itemId,
                "Monitor 4K",
                new BigDecimal("499.99"),
                1,
                new BigDecimal("499.99")
        );

        OrderResult orderResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                List.of(itemResult),
                new BigDecimal("499.99"),
                createdAt
        );

        OrderItemResponse itemResponse = new OrderItemResponse(
                itemId,
                "Monitor 4K",
                new BigDecimal("499.99"),
                1,
                new BigDecimal("499.99")
        );

        OrderResponse expectedResponse = new OrderResponse(
                orderId,
                customerId,
                "DRAFT",
                List.of(itemResponse),
                new BigDecimal("499.99"),
                createdAt
        );

        when(findOrderByIdUseCase.execute(orderId)).thenReturn(orderResult);
        when(orderResponseMapper.toResponse(orderResult)).thenReturn(expectedResponse);

        // When
        ResponseEntity<OrderResponse> response = viewOrdersController.findOrderById(orderId);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(orderId, response.getBody().id());
        assertEquals(customerId, response.getBody().customerId());
        assertEquals("DRAFT", response.getBody().status());
        assertEquals(1, response.getBody().items().size());
        assertEquals(new BigDecimal("499.99"), response.getBody().total());
        assertEquals(createdAt, response.getBody().createdAt());

        verify(findOrderByIdUseCase).execute(orderId);
        verify(orderResponseMapper).toResponse(orderResult);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order not found by ID")
    void findOrderById_ShouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
        // Given
        UUID orderId = UUID.randomUUID();

        when(findOrderByIdUseCase.execute(orderId))
                .thenThrow(new ResourceNotFoundException("Order not found for ID: " + orderId));

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> viewOrdersController.findOrderById(orderId)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(findOrderByIdUseCase).execute(orderId);
        verifyNoInteractions(orderResponseMapper);
    }
}
