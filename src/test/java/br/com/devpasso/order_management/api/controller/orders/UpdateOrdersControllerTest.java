package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.orders.request.AddItemToOrderRequest;
import br.com.devpasso.order_management.api.dto.orders.response.OrderItemResponse;
import br.com.devpasso.order_management.api.mapper.orders.request.AddItemToOrderRequestMapper;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderItemResponseMapper;
import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.exception.BusinessException;
import br.com.devpasso.order_management.application.usecase.orders.AddItemToOrderUseCase;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateOrdersControllerTest {

    @Mock
    private AddItemToOrderUseCase addItemToOrderUseCase;

    @Mock
    private AddItemToOrderRequestMapper addItemToOrderRequestMapper;

    @Mock
    private OrderItemResponseMapper orderItemResponseMapper;

    @InjectMocks
    private UpdateOrdersController updateOrdersController;

    @Test
    @DisplayName("Should add item to order and return 200 OK status with order item response")
    void addItemToOrder_ShouldReturnOkWithOrderItemResponse() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        int quantity = 2;

        AddItemToOrderRequest request = new AddItemToOrderRequest(productId, quantity);
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, quantity);

        OrderItemResult result = new OrderItemResult(
                itemId,
                "Gaming Headset",
                new BigDecimal("99.99"),
                quantity,
                new BigDecimal("199.98")
        );

        OrderItemResponse expectedResponse = new OrderItemResponse(
                itemId,
                "Gaming Headset",
                new BigDecimal("99.99"),
                quantity,
                new BigDecimal("199.98")
        );

        when(addItemToOrderRequestMapper.toCommand(orderId, request)).thenReturn(command);
        when(addItemToOrderUseCase.execute(command)).thenReturn(result);
        when(orderItemResponseMapper.toResponse(result)).thenReturn(expectedResponse);

        // When
        ResponseEntity<OrderItemResponse> response = updateOrdersController.addItemToOrder(orderId, request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(expectedResponse, response.getBody());
        assertEquals(itemId, response.getBody().id());
        assertEquals("Gaming Headset", response.getBody().productName());
        assertEquals(new BigDecimal("99.99"), response.getBody().unitPrice());
        assertEquals(quantity, response.getBody().quantity());
        assertEquals(new BigDecimal("199.98"), response.getBody().subtotal());

        verify(addItemToOrderRequestMapper).toCommand(orderId, request);
        verify(addItemToOrderUseCase).execute(command);
        verify(orderItemResponseMapper).toResponse(result);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order or product is not found")
    void addItemToOrder_ShouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderRequest request = new AddItemToOrderRequest(productId, 1);
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, 1);

        when(addItemToOrderRequestMapper.toCommand(orderId, request)).thenReturn(command);
        when(addItemToOrderUseCase.execute(command))
                .thenThrow(new ResourceNotFoundException("Order not found for ID: " + orderId));

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateOrdersController.addItemToOrder(orderId, request)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(addItemToOrderRequestMapper).toCommand(orderId, request);
        verify(addItemToOrderUseCase).execute(command);
        verifyNoInteractions(orderItemResponseMapper);
    }

    @Test
    @DisplayName("Should throw BusinessException when product has insufficient stock")
    void addItemToOrder_ShouldThrowBusinessExceptionWhenInsufficientStock() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderRequest request = new AddItemToOrderRequest(productId, 10);
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, 10);

        when(addItemToOrderRequestMapper.toCommand(orderId, request)).thenReturn(command);
        when(addItemToOrderUseCase.execute(command))
                .thenThrow(new BusinessException("Unsufficient stock quantity for: Wireless Mouse. Available: 2, Requested: 10"));

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> updateOrdersController.addItemToOrder(orderId, request)
        );

        assertTrue(exception.getMessage().contains("Unsufficient stock quantity"));
        verify(addItemToOrderRequestMapper).toCommand(orderId, request);
        verify(addItemToOrderUseCase).execute(command);
        verifyNoInteractions(orderItemResponseMapper);
    }
}
