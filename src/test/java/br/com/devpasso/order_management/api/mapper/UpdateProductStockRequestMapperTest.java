package br.com.devpasso.order_management.api.mapper;

import br.com.devpasso.order_management.api.dto.request.UpdateProductStockRequest;
import br.com.devpasso.order_management.api.mapper.request.UpdateProductStockRequestMapper;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UpdateProductStockRequestMapperTest {

    private final UpdateProductStockRequestMapper mapper = new UpdateProductStockRequestMapper();

    @Test
    @DisplayName("Should map update product stock request to update product stock command")
    void toCommand_ShouldMapUpdateProductStockRequestToUpdateProductStockCommand() {
        // Given
        UpdateProductStockRequest request = new UpdateProductStockRequest(50);

        // When
        UpdateProductStockCommand command = mapper.toCommand(request);

        // Then
        assertNotNull(command);
        assertEquals(request.stockQuantity(), command.stockQuantity());
    }
}
