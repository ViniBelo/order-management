package br.com.devpasso.order_management.application.mapper;

import br.com.devpasso.order_management.api.dto.response.ProductResponse;
import br.com.devpasso.order_management.api.mapper.response.ProductResponseMapper;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ProductResultMapperTest {

    private final ProductResponseMapper mapper = new ProductResponseMapper();

    @Test
    @DisplayName("Should map product to product response")
    void toResponse_ShouldMapProductToProductResponse() {
        UUID id = UUID.randomUUID();
        String name = "Test Product";
        String description = "Test Description";
        BigDecimal price = new BigDecimal("99.99");
        Integer stockQuantity = 10;

        ProductResult product = new ProductResult(id,
                name,
                description,
                price,
                stockQuantity);

        ProductResponse response = mapper.toResponse(product);

        assertNotNull(response);
        assertEquals(id, response.id());
        assertEquals(name, response.name());
        assertEquals(description, response.description());
        assertEquals(price, response.price());
        assertEquals(stockQuantity, response.stockQuantity());
    }
}
