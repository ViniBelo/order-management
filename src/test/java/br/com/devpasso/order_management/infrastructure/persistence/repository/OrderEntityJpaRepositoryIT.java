package br.com.devpasso.order_management.infrastructure.persistence.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OrderEntityJpaRepositoryIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Test
    @DisplayName("should persist order entity")
    void save_shouldPersistOrderEntity() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderStatusEntity orderStatusEntity = OrderStatusEntity.DRAFT;
        Instant createdAt = Instant.now();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(orderStatusEntity);
        orderEntity.changeCreatedAt(createdAt);

        // When
        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);

        // Then
        assertThat(savedOrder).isNotNull();
        assertThat(savedOrder.getId()).isNotNull();
        assertEquals(customerId, savedOrder.getCustomerId());
        assertEquals(orderStatusEntity, savedOrder.getStatus());
        assertEquals(createdAt, savedOrder.getCreatedAt());
    }

    @Test
    @DisplayName("should find order by id when order exists")
    void findById_shouldReturnOrderWhenExists() {
        // Given
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderStatusEntity orderStatusEntity = OrderStatusEntity.DRAFT;
        Instant createdAt = Instant.now();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(orderStatusEntity);
        orderEntity.changeCreatedAt(createdAt);

        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);

        // When
        Optional<OrderEntity> foundOrder = orderJpaRepository.findById(savedOrder.getId());

        // Then
        assertTrue(foundOrder.isPresent());
        assertEquals(savedOrder.getId(), foundOrder.get().getId());
        assertEquals(customerId, foundOrder.get().getCustomerId());
        assertEquals(orderStatusEntity, foundOrder.get().getStatus());
        assertEquals(createdAt, foundOrder.get().getCreatedAt());
    }

    @Test
    @DisplayName("should return empty optional when order does not exist by id")
    void findById_shouldReturnEmptyWhenOrderDoesNotExist() {
        // Given
        UUID nonExistentId = UUID.randomUUID();

        // When
        Optional<OrderEntity> foundOrder = orderJpaRepository.findById(nonExistentId);

        // Then
        assertTrue(foundOrder.isEmpty());
    }

    @Test
    @DisplayName("should return orders matching the requested status")
    void findAllByStatus_shouldReturnMatchingOrders() {
        // Given
        OrderEntity draftOrder1 = new OrderEntity();
        draftOrder1.changeId(UUID.randomUUID());
        draftOrder1.changeCustomerId(UUID.randomUUID());
        draftOrder1.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder1.changeCreatedAt(Instant.now());

        OrderEntity draftOrder2 = new OrderEntity();
        draftOrder2.changeId(UUID.randomUUID());
        draftOrder2.changeCustomerId(UUID.randomUUID());
        draftOrder2.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder2.changeCreatedAt(Instant.now());

        OrderEntity confirmedOrder = new OrderEntity();
        confirmedOrder.changeId(UUID.randomUUID());
        confirmedOrder.changeCustomerId(UUID.randomUUID());
        confirmedOrder.changeStatus(OrderStatusEntity.CONFIRMED);
        confirmedOrder.changeCreatedAt(Instant.now());

        orderJpaRepository.saveAll(List.of(draftOrder1, draftOrder2, confirmedOrder));

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrderEntity> draftPage = orderJpaRepository.findAllByStatus(pageable, OrderStatusEntity.DRAFT);

        // Then
        assertEquals(2, draftPage.getTotalElements());
        assertEquals(2, draftPage.getContent().size());
        assertTrue(draftPage.getContent().stream().allMatch(o -> o.getStatus() == OrderStatusEntity.DRAFT));
    }

    @Test
    @DisplayName("should return empty page when no orders match the requested status")
    void findAllByStatus_shouldReturnEmptyWhenNoMatch() {
        // Given
        OrderEntity draftOrder = new OrderEntity();
        draftOrder.changeId(UUID.randomUUID());
        draftOrder.changeCustomerId(UUID.randomUUID());
        draftOrder.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder.changeCreatedAt(Instant.now());

        orderJpaRepository.save(draftOrder);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<OrderEntity> page = orderJpaRepository.findAllByStatus(pageable, OrderStatusEntity.CANCELLED);

        // Then
        assertTrue(page.isEmpty());
        assertEquals(0, page.getTotalElements());
    }
}
