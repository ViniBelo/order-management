package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class DeleteOrdersController extends OrdersController {
    private final DeleteOrderUseCase deleteOrderUseCase;

    public DeleteOrdersController(DeleteOrderUseCase deleteOrderUseCase) {
        this.deleteOrderUseCase = deleteOrderUseCase;
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        deleteOrderUseCase.execute(id);
        return ResponseEntity.noContent()
                .build();
    }
}
