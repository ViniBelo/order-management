package br.com.devpasso.order_management.infrastructure.persistence.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    @DisplayName("Should throw exception when inserting duplicate active product name")
    void shouldThrowExceptionWhenDuplicateActiveProductName() {
        // Given
        ProductEntity product1 = new ProductEntity();
        product1.changeName("Keyboard");
        product1.changePrice(new BigDecimal("150.00"));
        product1.changeStockQuantity(10);
        repository.saveAndFlush(product1);

        ProductEntity product2 = new ProductEntity();
        product2.changeName("Keyboard");
        product2.changePrice(new BigDecimal("200.00"));
        product2.changeStockQuantity(5);

        // When & Then
        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(product2));
    }

    @Test
    @DisplayName("Should allow same product name when previous product is soft deleted")
    void shouldAllowSameNameWhenPreviousProductIsSoftDeleted() {
        // Given
        ProductEntity deletedProduct = new ProductEntity();
        deletedProduct.changeName("Mouse");
        deletedProduct.changePrice(new BigDecimal("50.00"));
        deletedProduct.changeStockQuantity(10);
        deletedProduct.changeDeletedAt(Instant.now());
        repository.saveAndFlush(deletedProduct);

        ProductEntity activeProduct = new ProductEntity();
        activeProduct.changeName("Mouse");
        activeProduct.changePrice(new BigDecimal("60.00"));
        activeProduct.changeStockQuantity(20);

        // When
        ProductEntity savedActiveProduct = repository.saveAndFlush(activeProduct);

        // Then
        assertNotNull(savedActiveProduct.getId());
        assertEquals("Mouse", savedActiveProduct.getName());
    }

    @Test
    @DisplayName("Should allow multiple soft deleted products with the same name")
    void shouldAllowMultipleSoftDeletedProductsWithSameName() {
        // Given
        ProductEntity deletedProduct1 = new ProductEntity();
        deletedProduct1.changeName("Monitor");
        deletedProduct1.changePrice(new BigDecimal("300.00"));
        deletedProduct1.changeStockQuantity(5);
        deletedProduct1.changeDeletedAt(Instant.now());
        repository.saveAndFlush(deletedProduct1);

        ProductEntity deletedProduct2 = new ProductEntity();
        deletedProduct2.changeName("Monitor");
        deletedProduct2.changePrice(new BigDecimal("350.00"));
        deletedProduct2.changeStockQuantity(2);
        deletedProduct2.changeDeletedAt(Instant.now());

        // When
        ProductEntity savedDeletedProduct2 = repository.saveAndFlush(deletedProduct2);

        // Then
        assertNotNull(savedDeletedProduct2.getId());
        assertEquals("Monitor", savedDeletedProduct2.getName());
    }
}
