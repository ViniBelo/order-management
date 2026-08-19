package br.com.devpasso.order_management.infrastructure.persistence.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
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

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductEntityJpaRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProductJpaRepository repository;

    @Test
    @DisplayName("Should return products when name contains the search term")
    void findAllByNameContainingIgnoreCase_ShouldReturnMatchingProducts() {
        // Given
        ProductEntity productEntity1 = new ProductEntity();
        productEntity1.changeName("Laptop");
        productEntity1.changePrice(new BigDecimal("1000.00"));
        productEntity1.changeStockQuantity(10);
        repository.save(productEntity1);

        ProductEntity productEntity2 = new ProductEntity();
        productEntity2.changeName("Desktop");
        productEntity2.changePrice(new BigDecimal("800.00"));
        productEntity2.changeStockQuantity(5);
        repository.save(productEntity2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ProductEntity> result = repository.findAllByNameContainingIgnoreCase(pageable, "lap");

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent()
                .getFirst()
                .getName());
    }

    @Test
    @DisplayName("Should return empty when no products match the search term")
    void findAllByNameContainingIgnoreCase_ShouldReturnEmptyWhenNoMatch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<ProductEntity> result = repository.findAllByNameContainingIgnoreCase(pageable, "NonExistent");

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return true when product exists by name")
    void existsByName_ShouldReturnTrueWhenProductExists() {
        // Given
        ProductEntity productEntity = new ProductEntity();
        productEntity.changeName("Tablet");
        productEntity.changePrice(new BigDecimal("500.00"));
        productEntity.changeStockQuantity(15);
        repository.save(productEntity);

        // When
        boolean exists = repository.existsByName("Tablet");

        // Then
        assertTrue(exists);
    }

    @Test
    @DisplayName("Should return false when product does not exist by name")
    void existsByName_ShouldReturnFalseWhenProductDoesNotExist() {
        // When
        boolean exists = repository.existsByName("NonExistentProduct");

        // Then
        assertFalse(exists);
    }
}
