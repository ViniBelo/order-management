package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.OrderEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.OrderJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OrderRepositoryAdapterTest {
    @Mock
    private OrderJpaRepository orderJpaRepository;

    @Mock
    private OrderEntityMapper orderEntityMapper;

    @InjectMocks
    private OrderRepositoryAdapter orderRepositoryAdapter;

    @Test
    @DisplayName("Should save order and map persisted entity to model")
    void save_shouldSaveOrderAndMapPersistedEntityToModel() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderStatus orderStatus = OrderStatus.DRAFT;
        List<OrderItem> orderItems = List.of();
        Instant createdAt = Instant.now();
        final Order order = new Order(
                id,
                customerId,
                orderStatus,
                orderItems,
                createdAt
        );

        OrderStatusEntity orderStatusEntity = OrderStatusEntity.DRAFT;
        List<OrderItemEntity> orderItemsEntity = List.of();
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(orderStatusEntity);
        orderEntity.changeItems(orderItemsEntity);
        orderEntity.changeCreatedAt(createdAt);

        when(orderEntityMapper.toEntity(order)).thenReturn(orderEntity);
        when(orderJpaRepository.save(orderEntity)).thenReturn(orderEntity);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(order);
        // When
        Order result = orderRepositoryAdapter.save(order);

        // Then
        assertEquals(order, result);
        verify(orderEntityMapper).toDomain(orderEntity);
        verify(orderEntityMapper).toEntity(order);
        verify(orderJpaRepository).save(orderEntity);
    }

    @Test
    @DisplayName("Should delegate to OrderJpaRepository to find an order")
    void findById_shouldDelegateToOrderJpaRepositoryToFindAnOrder() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderStatus orderStatus = OrderStatus.DRAFT;
        List<OrderItem> orderItems = List.of();
        Instant createdAt = Instant.now();
        final Order order = new Order(
                orderId,
                customerId,
                orderStatus,
                orderItems,
                createdAt
        );

        when(orderJpaRepository.findById(orderId))
                .thenReturn(Optional.of(new OrderEntity()));
        when(orderEntityMapper.toDomain(any(OrderEntity.class)))
                .thenReturn(order);

        // When
        Optional<Order> result = orderRepositoryAdapter.findById(orderId);

        // Then
        assertEquals(order, result.get());
        verify(orderJpaRepository).findById(orderId);
        verify(orderEntityMapper).toDomain(any(OrderEntity.class));
    }
}