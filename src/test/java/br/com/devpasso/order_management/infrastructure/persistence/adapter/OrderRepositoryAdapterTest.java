package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Order;
import br.com.devpasso.order_management.domain.model.OrderItem;
import br.com.devpasso.order_management.domain.model.OrderStatus;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Collections;
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
    private InfraPaginationMapper paginationMapper;

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

    @Test
    @DisplayName("Should delegate to JPA repository and map to domain for findByStatus")
    void findByStatus_shouldDelegateToJpaRepositoryAndMapToDomain() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "createdAt,ASC");
        Pageable pageable = PageRequest.of(0, 10);
        OrderStatus status = OrderStatus.DRAFT;
        OrderStatusEntity statusEntity = OrderStatusEntity.DRAFT;

        OrderEntity orderEntity = new OrderEntity();
        Page<OrderEntity> entityPage = new PageImpl<>(List.of(orderEntity));

        Order domainOrder = new Order(
                UUID.randomUUID(),
                UUID.randomUUID(),
                status,
                Collections.emptyList(),
                Instant.now()
        );

        PaginatedQueryResult<Order> expectedResult = new PaginatedQueryResult<>(
                List.of(domainOrder), 0, 10, 1, 1
        );

        when(paginationMapper.toSpringPageable(paginationQuery)).thenReturn(pageable);
        when(orderJpaRepository.findAllByStatus(pageable, statusEntity)).thenReturn(entityPage);
        when(orderEntityMapper.toDomain(orderEntity)).thenReturn(domainOrder);
        when(paginationMapper.toDomainResult(entityPage, List.of(domainOrder))).thenReturn(expectedResult);

        // When
        PaginatedQueryResult<Order> result = orderRepositoryAdapter.findByStatus(paginationQuery, status);

        // Then
        assertEquals(1, result.totalElements());
        assertEquals(domainOrder, result.content().getFirst());

        verify(paginationMapper).toSpringPageable(paginationQuery);
        verify(orderJpaRepository).findAllByStatus(pageable, statusEntity);
        verify(orderEntityMapper).toDomain(orderEntity);
        verify(paginationMapper).toDomainResult(entityPage, List.of(domainOrder));
    }

    @Test
    @DisplayName("Should return empty result when no orders found by status")
    void findByStatus_shouldReturnEmptyResultWhenNoOrdersFound() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "createdAt,ASC");
        Pageable pageable = PageRequest.of(0, 10);
        OrderStatus status = OrderStatus.CANCELLED;
        OrderStatusEntity statusEntity = OrderStatusEntity.CANCELLED;

        Page<OrderEntity> emptyEntityPage = new PageImpl<>(Collections.emptyList());
        List<Order> emptyOrdersList = List.of();
        PaginatedQueryResult<Order> emptyResult = new PaginatedQueryResult<>(
                emptyOrdersList, 0, 10, 0, 0
        );

        when(paginationMapper.toSpringPageable(paginationQuery)).thenReturn(pageable);
        when(orderJpaRepository.findAllByStatus(pageable, statusEntity)).thenReturn(emptyEntityPage);
        when(paginationMapper.toDomainResult(emptyEntityPage, emptyOrdersList)).thenReturn(emptyResult);

        // When
        PaginatedQueryResult<Order> result = orderRepositoryAdapter.findByStatus(paginationQuery, status);

        // Then
        assertEquals(0, result.totalElements());
        assertEquals(0, result.content().size());

        verify(paginationMapper).toSpringPageable(paginationQuery);
        verify(orderJpaRepository).findAllByStatus(pageable, statusEntity);
        verify(paginationMapper).toDomainResult(emptyEntityPage, Collections.emptyList());
    }
}