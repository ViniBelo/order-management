package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.dto.result.orders.OrderItemResult;
import br.com.devpasso.order_management.application.exception.BusinessException;
import br.com.devpasso.order_management.application.mapper.orders.OrderItemResultMapper;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AddItemToOrderServiceTest {

    @Mock
    private OrderItemResultMapper orderItemResultMapper;

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @Mock
    private ProductRepositoryPort productRepositoryPort;

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

        OrderItemResult expectedResult = new OrderItemResult(
                null,
                "Mechanical Keyboard",
                new BigDecimal("150.00"),
                quantity,
                new BigDecimal("300.00")
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepositoryPort.findById(productId.toString())).thenReturn(Optional.of(product));
        when(orderItemResultMapper.map(any(OrderItem.class))).thenReturn(expectedResult);

        // When
        OrderItemResult result = addItemToOrderService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(1, order.items().size());
        assertEquals(productId, order.items().getFirst().productId());
        assertEquals("Mechanical Keyboard", order.items().getFirst().productName());
        assertEquals(new BigDecimal("150.00"), order.items().getFirst().unitPrice());
        assertEquals(quantity, order.items().getFirst().quantity());

        verify(orderRepositoryPort).findById(orderId);
        verify(productRepositoryPort).findById(productId.toString());
        verify(orderRepositoryPort).save(order);
        verify(orderItemResultMapper).map(any(OrderItem.class));
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

        OrderItemResult expectedResult = new OrderItemResult(
                null,
                "Mouse Pad",
                new BigDecimal("30.00"),
                quantity,
                new BigDecimal("150.00")
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepositoryPort.findById(productId.toString())).thenReturn(Optional.of(product));
        when(orderItemResultMapper.map(any(OrderItem.class))).thenReturn(expectedResult);

        // When
        OrderItemResult result = addItemToOrderService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        verify(orderRepositoryPort).save(order);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order does not exist")
    void shouldThrowResourceNotFoundExceptionWhenOrderDoesNotExist() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, productId, 2);

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> addItemToOrderService.execute(command)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(orderRepositoryPort).findById(orderId);
        verifyNoInteractions(productRepositoryPort);
        verify(orderRepositoryPort, never()).save(any());
        verifyNoInteractions(orderItemResultMapper);
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

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepositoryPort.findById(productId.toString())).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> addItemToOrderService.execute(command)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(orderRepositoryPort).findById(orderId);
        verify(productRepositoryPort).findById(productId.toString());
        verify(orderRepositoryPort, never()).save(any());
        verifyNoInteractions(orderItemResultMapper);
    }

    @Test
    @DisplayName("Should add item to order successfully when order already has existing items")
    void shouldAddItemToOrderWhenOrderAlreadyHasExistingItems() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID existingProductId = UUID.randomUUID();
        UUID newProductId = UUID.randomUUID();
        int quantity = 3;
        AddItemToOrderCommand command = new AddItemToOrderCommand(orderId, newProductId, quantity);

        OrderItem existingItem = new OrderItem(
                UUID.randomUUID(),
                existingProductId,
                "Existing Product",
                new BigDecimal("50.00"),
                1
        );
        List<OrderItem> items = new ArrayList<>();
        items.add(existingItem);

        Order order = new Order(
                orderId,
                UUID.randomUUID(),
                OrderStatus.DRAFT,
                items,
                Instant.now()
        );

        Product newProduct = new Product(
                newProductId,
                "New Product",
                "New Product Description",
                new BigDecimal("80.00"),
                10,
                Instant.now()
        );

        OrderItemResult expectedResult = new OrderItemResult(
                null,
                "New Product",
                new BigDecimal("80.00"),
                quantity,
                new BigDecimal("240.00")
        );

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepositoryPort.findById(newProductId.toString())).thenReturn(Optional.of(newProduct));
        when(orderItemResultMapper.map(any(OrderItem.class))).thenReturn(expectedResult);

        // When
        OrderItemResult result = addItemToOrderService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(expectedResult, result);
        assertEquals(2, order.items().size());
        assertEquals(existingProductId, order.items().get(0).productId());
        assertEquals(newProductId, order.items().get(1).productId());
        assertEquals("New Product", order.items().get(1).productName());
        assertEquals(new BigDecimal("80.00"), order.items().get(1).unitPrice());
        assertEquals(quantity, order.items().get(1).quantity());

        verify(orderRepositoryPort).findById(orderId);
        verify(productRepositoryPort).findById(newProductId.toString());
        verify(orderRepositoryPort).save(order);
        verify(orderItemResultMapper).map(any(OrderItem.class));
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

        when(orderRepositoryPort.findById(orderId)).thenReturn(Optional.of(order));
        when(productRepositoryPort.findById(productId.toString())).thenReturn(Optional.of(product));

        // When & Then
        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> addItemToOrderService.execute(command)
        );

        String expectedMessage = "Unsufficient stock quantity for: " + product.getName() +
                "Available: " + product.getStockQuantity() +
                "Requested: " + requestedQuantity;

        assertEquals(expectedMessage, exception.getMessage());
        verify(orderRepositoryPort).findById(orderId);
        verify(productRepositoryPort).findById(productId.toString());
        verify(orderRepositoryPort, never()).save(any());
        verifyNoInteractions(orderItemResultMapper);
    }
}
