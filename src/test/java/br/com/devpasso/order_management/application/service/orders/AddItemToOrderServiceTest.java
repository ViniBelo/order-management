package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.AddItemToOrderResult;
import br.com.devpasso.order_management.application.exception.BusinessException;
import br.com.devpasso.order_management.application.mapper.orders.AddItemToOrderResultMapper;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.OrderRepository;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddItemToOrderServiceTest {

    @Mock
    private AddItemToOrderResultMapper addItemToOrderResultMapper;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private AddItemToOrderService addItemToOrderService;

    @Test
    @DisplayName("Should add item to order successfully when order and product exist with sufficient stock")
    void shouldAddItemToOrderSuccessfully() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int quantity = 2;
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, quantity);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.DRAFT,
                new ArrayList<>(),
                Instant.now()
        );

        Product product = new Product(
                productId,
                "Mechanical Keyboard",
                "RGB Mechanical Keyboard",
                new BigDecimal("150.00"),
                10,
                Instant.now()
        );

        AddItemToOrderResult expectedResult = new AddItemToOrderResult(
                null,
                "Mechanical Keyboard",
                new BigDecimal("150.00"),
                quantity,
                new BigDecimal("300.00")
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId.toString())).thenReturn(Optional.of(product));
        when(addItemToOrderResultMapper.map(any(OrderItem.class))).thenReturn(expectedResult);

        // When
        AddItemToOrderResult result = addItemToOrderService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(1, order.items().size());
        assertEquals(productId, order.items().getFirst().productId());
        assertEquals("Mechanical Keyboard", order.items().getFirst().productName());
        assertEquals(new BigDecimal("150.00"), order.items().getFirst().unitPrice());
        assertEquals(quantity, order.items().getFirst().quantity());

        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(productId.toString());
        verify(orderRepository).save(order);
        verify(addItemToOrderResultMapper).map(any(OrderItem.class));
    }

    @Test
    @DisplayName("Should add item to order successfully when stock exactly equals requested quantity")
    void shouldAddItemToOrderWhenStockEqualsRequestedQuantity() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int quantity = 5;
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, quantity);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.DRAFT,
                new ArrayList<>(),
                Instant.now()
        );

        Product product = new Product(
                productId,
                "Mouse Pad",
                "Large Mouse Pad",
                new BigDecimal("30.00"),
                5,
                Instant.now()
        );

        AddItemToOrderResult expectedResult = new AddItemToOrderResult(
                null,
                "Mouse Pad",
                new BigDecimal("30.00"),
                quantity,
                new BigDecimal("150.00")
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId.toString())).thenReturn(Optional.of(product));
        when(addItemToOrderResultMapper.map(any(OrderItem.class))).thenReturn(expectedResult);

        // When
        AddItemToOrderResult result = addItemToOrderService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(orderRepository).save(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order does not exist")
    void shouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, 2);

        when(orderRepository.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> addItemToOrderService.execute(command)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(orderRepository).findById(orderId);
        verifyNoInteractions(productRepository);
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(addItemToOrderResultMapper);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product does not exist")
    void shouldThrowResourceNotFoundExceptionWhenProductDoesNotExist() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, 2);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.DRAFT,
                new ArrayList<>(),
                Instant.now()
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId.toString())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> addItemToOrderService.execute(command)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(productId.toString());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(addItemToOrderResultMapper);
    }

    @Test
    @DisplayName("Should throw BusinessException when stock quantity is insufficient")
    void shouldThrowBusinessExceptionWhenStockQuantityIsInsufficient() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        int requestedQuantity = 5;
        int availableStock = 2;
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, requestedQuantity);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.DRAFT,
                new ArrayList<>(),
                Instant.now()
        );

        Product product = new Product(
                productId,
                "Monitor 4K",
                "4K IPS Monitor",
                new BigDecimal("2000.00"),
                availableStock,
                Instant.now()
        );

        when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepository.findById(productId.toString())).thenReturn(Optional.of(product));

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> addItemToOrderService.execute(command)
        );

        String expectedMessage = "Unsufficient stock quantity for: " + product.getName() +
                "Available: " + product.getStockQuantity() +
                "Requested: " + requestedQuantity;

        assertEquals(expectedMessage, exception.getMessage());
        verify(orderRepository).findById(orderId);
        verify(productRepository).findById(productId.toString());
        verify(orderRepository, never()).save(any());
        verifyNoInteractions(addItemToOrderResultMapper);
    }
}
