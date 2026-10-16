package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateOrdersServiceTest {

    @Mock
    private OrderResultMapper orderResultMapper;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CreateOrdersService createOrdersService;

    @Test
    @DisplayName("Should create order successfully and return mapped result")
    void shouldCreateOrderSuccessfully() {
        // Given
        UUID customerId = UUID.randomUUID();
        CreateOrderCommand command = new CreateOrderCommand(customerId);

        UUID orderId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Order savedOrder = new Order(
                orderId,
                customerId,
                OrderStatus.DRAFT,
                Collections.emptyList(),
                createdAt
        );

        OrderResult expectedResult = new OrderResult(
                orderId,
                customerId,
                "DRAFT",
                Collections.emptyList(),
                BigDecimal.ZERO,
                createdAt
        );

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderResultMapper.map(savedOrder)).thenReturn(expectedResult);

        // When
        OrderResult result = createOrdersService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);

        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(orderCaptor.capture());
        Order capturedOrder = orderCaptor.getValue();
        assertNotNull(capturedOrder);
        assertEquals(customerId, capturedOrder.customerId());
        assertEquals(OrderStatus.DRAFT, capturedOrder.status());
        assertTrue(capturedOrder.items().isEmpty());

        verify(orderResultMapper).map(savedOrder);
    }
}
