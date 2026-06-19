package br.com.devpasso.order_management.infrastructure.persistence.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.Product;
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
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductJpaRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private ProductJpaRepository repository;

    @Test
    void findAllByNameContainingIgnoreCase_ShouldReturnMatchingProducts() {
        // Given
        Product product1 = new Product();
        product1.changeName("Laptop");
        product1.changePrice(new BigDecimal("1000.00"));
        product1.changeStockQuantity(10);
        repository.save(product1);

        Product product2 = new Product();
        product2.changeName("Desktop");
        product2.changePrice(new BigDecimal("800.00"));
        product2.changeStockQuantity(5);
        repository.save(product2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Product> result = repository.findAllByNameContainingIgnoreCase(pageable, "lap");

        // Then
        assertEquals(1, result.getTotalElements());
        assertEquals("Laptop", result.getContent()
                .getFirst()
                .getName());
    }

    @Test
    void findAllByNameContainingIgnoreCase_ShouldReturnEmptyWhenNoMatch() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        // When
        Page<Product> result = repository.findAllByNameContainingIgnoreCase(pageable, "NonExistent");

        // Then
        assertTrue(result.isEmpty());
    }
}
