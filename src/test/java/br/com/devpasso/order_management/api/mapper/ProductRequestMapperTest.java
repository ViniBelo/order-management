package br.com.devpasso.order_management.api.mapper;

import br.com.devpasso.order_management.api.dto.CreateProductRequest;
import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductRequestMapperTest {

    private final ProductRequestMapper mapper = new ProductRequestMapper();

    @Test
    @DisplayName("Should map create product request to create product command")
    void toCommand_ShouldMapCreateProductRequestToCreateProductCommand() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "Smartphone",
                "Latest generation smartphone",
                new BigDecimal("799.99"),
                25
        );

        // When
        CreateProductCommand command = mapper.toCommand(request);

        // Then
        assertNotNull(command);
        assertEquals(request.name(), command.name());
        assertEquals(request.description(), command.description());
        assertEquals(request.price(), command.price());
        assertEquals(request.stockQuantity(), command.stockQuantity());
    }
}
