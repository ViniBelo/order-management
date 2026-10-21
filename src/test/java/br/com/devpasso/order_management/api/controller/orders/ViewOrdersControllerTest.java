package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.request.orders.OrderStatusRequest;
import br.com.devpasso.order_management.api.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.api.dto.response.orders.OrderItemResponse;
import br.com.devpasso.order_management.api.dto.response.orders.OrderResponse;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderResponseMapper;
import br.com.devpasso.order_management.api.mapper.products.response.PaginatedResponseMapper;
import br.com.devpasso.order_management.application.dto.command.orders.FilterOrdersByStatusCommand;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.PaginationQueryMapper;
import br.com.devpasso.order_management.application.usecase.orders.FindOrderByIdUseCase;
import br.com.devpasso.order_management.application.usecase.orders.ListOrdersUseCase;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewOrdersControllerTest {

    @Mock
    private FindOrderByIdUseCase findOrderByIdUseCase;

    @Mock
    private ListOrdersUseCase listOrdersUseCase;

    @Mock
    private OrderResponseMapper orderResponseMapper;

    @Mock
    private PaginationQueryMapper paginationQueryMapper;

    @Mock
    private PaginatedResponseMapper paginatedResponseMapper;

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

    @Test
    @DisplayName("Should return paginated orders with 200 OK status")
    void listAll_ShouldReturnOkWithPaginatedOrders() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "createdAt,ASC");
        Pageable pageable = PageRequest.of(0, 20);
        OrderStatusRequest statusRequest = OrderStatusRequest.DRAFT;
        FilterOrdersByStatusCommand command = FilterOrdersByStatusCommand.DRAFT;

        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        OrderResult orderResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        OrderResponse orderResponse = new OrderResponse(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        PaginatedResult<OrderResult> paginatedResult = new PaginatedResult<>(
                List.of(orderResult), 0, 20, 1, 1
        );

        PaginatedResponse<OrderResponse> paginatedResponse = new PaginatedResponse<>(
                List.of(orderResponse), 0, 20, 1, 1
        );

        when(paginationQueryMapper.toDomainQuery(pageable)).thenReturn(paginationQuery);
        when(listOrdersUseCase.execute(paginationQuery, command)).thenReturn(paginatedResult);
        when(orderResponseMapper.toResponse(orderResult)).thenReturn(orderResponse);
        when(paginatedResponseMapper.from(paginatedResult, List.of(orderResponse))).thenReturn(paginatedResponse);

        // When
        ResponseEntity<PaginatedResponse<OrderResponse>> response = viewOrdersController.listAll(pageable, statusRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().totalElements());
        assertEquals(1, response.getBody().totalPages());
        assertEquals(0, response.getBody().page());
        assertEquals(20, response.getBody().size());
        assertEquals(1, response.getBody().content().size());
        assertEquals(orderResponse, response.getBody().content().getFirst());

        verify(paginationQueryMapper).toDomainQuery(pageable);
        verify(listOrdersUseCase).execute(paginationQuery, command);
        verify(orderResponseMapper).toResponse(orderResult);
        verify(paginatedResponseMapper).from(paginatedResult, List.of(orderResponse));
    }

    @Test
    @DisplayName("Should return empty paginated response when no orders found")
    void listAll_ShouldReturnEmptyPaginatedResponseWhenNoOrdersFound() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "createdAt,ASC");
        Pageable pageable = PageRequest.of(0, 20);
        OrderStatusRequest statusRequest = OrderStatusRequest.CONFIRMED;
        FilterOrdersByStatusCommand command = FilterOrdersByStatusCommand.CONFIRMED;

        List<OrderResult> emptyOrderResultsList = List.of();
        PaginatedResult<OrderResult> paginatedResult = new PaginatedResult<>(
                emptyOrderResultsList, 0, 20, 0, 0
        );

        List<OrderResponse> emptyOrderResponsesList = List.of();
        PaginatedResponse<OrderResponse> paginatedResponse = new PaginatedResponse<>(
                emptyOrderResponsesList, 0, 20, 0, 0
        );

        when(paginationQueryMapper.toDomainQuery(pageable)).thenReturn(paginationQuery);
        when(listOrdersUseCase.execute(paginationQuery, command)).thenReturn(paginatedResult);
        when(paginatedResponseMapper.from(paginatedResult, emptyOrderResponsesList)).thenReturn(paginatedResponse);

        // When
        ResponseEntity<PaginatedResponse<OrderResponse>> response = viewOrdersController.listAll(pageable, statusRequest);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().totalElements());
        assertEquals(0, response.getBody().totalPages());
        assertTrue(response.getBody().content().isEmpty());

        verify(paginationQueryMapper).toDomainQuery(pageable);
        verify(listOrdersUseCase).execute(paginationQuery, command);
        verifyNoInteractions(orderResponseMapper);
        verify(paginatedResponseMapper).from(paginatedResult, Collections.emptyList());
    }
}
