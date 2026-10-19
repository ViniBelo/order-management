package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.orders.response.OrderResponse;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderResponseMapper;
import br.com.devpasso.order_management.application.usecase.orders.FindOrderByIdUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ViewOrdersController extends OrdersController {
    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final OrderResponseMapper orderResponseMapper;

    public ViewOrdersController(FindOrderByIdUseCase findOrderByIdUseCase,
                                OrderResponseMapper orderResponseMapper) {
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.orderResponseMapper = orderResponseMapper;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<OrderResponse> findOrderById(@PathVariable("id") UUID orderId) {
        OrderResponse response = orderResponseMapper.toResponse(findOrderByIdUseCase.execute(orderId));
        return ResponseEntity.ok(response);
    }
}
