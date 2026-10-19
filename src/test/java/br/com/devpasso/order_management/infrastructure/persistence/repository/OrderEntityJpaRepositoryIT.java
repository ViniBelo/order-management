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
        List<OrderItemEntity> orderItemEntities = List.of();
        Instant createdAt = Instant.now();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(orderStatusEntity);
        orderEntity.changeItems(orderItemEntities);
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
        List<OrderItemEntity> orderItemEntities = List.of();
        Instant createdAt = Instant.now();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(orderStatusEntity);
        orderEntity.changeItems(orderItemEntities);
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
}
