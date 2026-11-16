package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.request.orders.AddItemToOrderRequest;
import br.com.devpasso.order_management.api.dto.response.orders.OrderItemResponse;
import br.com.devpasso.order_management.api.mapper.orders.request.AddItemToOrderRequestMapper;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderItemResponseMapper;
import br.com.devpasso.order_management.application.dto.command.orders.AddItemToOrderCommand;
import br.com.devpasso.order_management.application.usecase.orders.AddItemToOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UpdateOrdersController extends OrdersController {
    private final AddItemToOrderUseCase addItemToOrderUseCase;
    private final AddItemToOrderRequestMapper addItemToOrderRequestMapper;
    private final OrderItemResponseMapper orderItemResponseMapper;

    public UpdateOrdersController(AddItemToOrderUseCase addItemToOrderUseCase,
                                  AddItemToOrderRequestMapper addItemToOrderRequestMapper,
                                  OrderItemResponseMapper orderItemResponseMapper) {
        this.addItemToOrderUseCase = addItemToOrderUseCase;
        this.addItemToOrderRequestMapper = addItemToOrderRequestMapper;
        this.orderItemResponseMapper = orderItemResponseMapper;
    }

    @PostMapping(value = "/{id}/items")
    @Operation(summary = "Add an item to an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Item added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<OrderItemResponse> addItemToOrder(@PathVariable("id") UUID orderId,
                                                            @RequestBody @Valid AddItemToOrderRequest request) {
        AddItemToOrderCommand command = addItemToOrderRequestMapper.toCommand(orderId, request);
        OrderItemResponse response = orderItemResponseMapper.toResponse(addItemToOrderUseCase.execute(command));
        return ResponseEntity.ok(response);
    }
}
