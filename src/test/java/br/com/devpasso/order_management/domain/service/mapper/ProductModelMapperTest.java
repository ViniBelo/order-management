package br.com.devpasso.order_management.domain.service.mapper;

import br.com.devpasso.order_management.domain.model.Product;
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

        br.com.devpasso.order_management.infrastructure.persistence.entity.Product persistenceProduct =
                new br.com.devpasso.order_management.infrastructure.persistence.entity.Product();

        ReflectionTestUtils.setField(persistenceProduct, "id", id);
        ReflectionTestUtils.setField(persistenceProduct, "createdAt", createdAt);

        persistenceProduct.changeName(name);
        persistenceProduct.changeDescription(description);
        persistenceProduct.changePrice(price);
        persistenceProduct.changeStockQuantity(stockQuantity);

        Product domainProduct = mapper.toModel(persistenceProduct);

        assertNotNull(domainProduct);
        assertEquals(id, domainProduct.getId());
        assertEquals(name, domainProduct.getName());
        assertEquals(description, domainProduct.getDescription());
        assertEquals(price, domainProduct.getPrice());
        assertEquals(stockQuantity, domainProduct.getStockQuantity());
    }
}
