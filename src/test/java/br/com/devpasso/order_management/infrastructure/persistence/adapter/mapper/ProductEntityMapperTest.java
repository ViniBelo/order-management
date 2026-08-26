package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductEntityMapperTest {

    private final ProductEntityMapper mapper = new ProductEntityMapper();

    @Test
    @DisplayName("Should map domain product to product entity")
    void toEntity_ShouldMapDomainProductToProductEntity() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        Integer stockQuantity = 10;
        Instant createdAt = Instant.now();

        Product product = new Product(id, name, description, price, stockQuantity, createdAt);

        // When
        ProductEntity entity = mapper.toEntity(product);

        // Then
        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(name, entity.getName());
        assertEquals(description, entity.getDescription());
        assertEquals(price, entity.getPrice());
        assertEquals(stockQuantity, entity.getStockQuantity());
        assertEquals(createdAt, entity.getCreatedAt());
    }
}
