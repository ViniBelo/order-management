package br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper;

import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductModelMapperTest {

    private final ProductModelMapper mapper = new ProductModelMapper();

    @Test
    void toModel_ShouldMapPersistenceProductToDomainProduct() {
        UUID id = UUID.randomUUID();
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        Integer stockQuantity = 10;
        Instant createdAt = Instant.now();

        ProductEntity ProductEntity =
                new ProductEntity();

        ReflectionTestUtils.setField(ProductEntity, "id", id);
        ReflectionTestUtils.setField(ProductEntity, "createdAt", createdAt);

        ProductEntity.changeName(name);
        ProductEntity.changeDescription(description);
        ProductEntity.changePrice(price);
        ProductEntity.changeStockQuantity(stockQuantity);

        Product domainProduct = mapper.toModel(ProductEntity);

        assertNotNull(domainProduct);
        assertEquals(id, domainProduct.getId());
        assertEquals(name, domainProduct.getName());
        assertEquals(description, domainProduct.getDescription());
        assertEquals(price, domainProduct.getPrice());
        assertEquals(stockQuantity, domainProduct.getStockQuantity());
    }
}
