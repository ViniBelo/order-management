package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.exception.ConflictErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.request.orders.CreateOrderRequest;
import br.com.devpasso.order_management.api.dto.response.orders.OrderResponse;
import br.com.devpasso.order_management.api.mapper.orders.request.CreateOrderRequestMapper;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderResponseMapper;
import br.com.devpasso.order_management.application.dto.command.orders.CreateOrderCommand;
import br.com.devpasso.order_management.application.usecase.orders.CreateOrderUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
public class CreateOrdersController extends OrdersController {
    private final CreateOrderUseCase createOrderUseCase;
    private final CreateOrderRequestMapper createOrderRequestMapper;
    private final OrderResponseMapper orderResponseMapper;

    public CreateOrdersController(CreateOrderUseCase createOrderUseCase,
                                  CreateOrderRequestMapper createOrderRequestMapper,
                                  OrderResponseMapper orderResponseMapper) {
        this.createOrderUseCase = createOrderUseCase;
        this.createOrderRequestMapper = createOrderRequestMapper;
        this.orderResponseMapper = orderResponseMapper;
    }

    @PostMapping
    @Operation(summary = "Create a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Product already exists",
                    content = @Content(schema = @Schema(implementation = ConflictErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<OrderResponse> createOrder(@RequestBody @Valid CreateOrderRequest request) {
        CreateOrderCommand command = createOrderRequestMapper.toCommand(request);
        OrderResponse response = orderResponseMapper.toResponse(createOrderUseCase.execute(command));
        return ResponseEntity.created(URI.create("/v1/orders/" + response.id()))
                .body(response);
    }
}
