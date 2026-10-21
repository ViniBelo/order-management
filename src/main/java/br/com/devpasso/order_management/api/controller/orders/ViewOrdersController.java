package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.request.orders.OrderStatusRequest;
import br.com.devpasso.order_management.api.dto.response.orders.OrderResponse;
import br.com.devpasso.order_management.api.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.api.mapper.orders.response.OrderResponseMapper;
import br.com.devpasso.order_management.api.mapper.products.response.PaginatedResponseMapper;
import br.com.devpasso.order_management.application.dto.command.orders.FilterOrdersByStatusCommand;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.orders.OrderResult;
import br.com.devpasso.order_management.application.mapper.PaginationQueryMapper;
import br.com.devpasso.order_management.application.usecase.orders.FindOrderByIdUseCase;
import br.com.devpasso.order_management.application.usecase.orders.ListOrdersUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class ViewOrdersController extends OrdersController {
    private final FindOrderByIdUseCase findOrderByIdUseCase;
    private final ListOrdersUseCase listOrdersUseCase;
    private final OrderResponseMapper orderResponseMapper;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginatedResponseMapper paginatedResponseMapper;

    public ViewOrdersController(FindOrderByIdUseCase findOrderByIdUseCase,
                                ListOrdersUseCase listOrdersUseCase,
                                OrderResponseMapper orderResponseMapper,
                                PaginationQueryMapper paginationQueryMapper,
                                PaginatedResponseMapper paginatedResponseMapper) {
        this.findOrderByIdUseCase = findOrderByIdUseCase;
        this.listOrdersUseCase = listOrdersUseCase;
        this.orderResponseMapper = orderResponseMapper;
        this.paginationQueryMapper = paginationQueryMapper;
        this.paginatedResponseMapper = paginatedResponseMapper;
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

    @GetMapping
    @Operation(summary = "List all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Orders found"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination, sort or filter parameter provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<OrderResponse>> listAll(
            @ParameterObject
            @PageableDefault(size = 20, sort = "createdAt")
            Pageable pageable,
            @Valid
            @Parameter(description = "Filter orders by status", example = "DRAFT")
            @RequestParam(defaultValue = "DRAFT")
            OrderStatusRequest status
    ) {
        PaginatedResult<OrderResult> paginatedResult = listOrdersUseCase.execute(
                paginationQueryMapper.toDomainQuery(pageable),
                FilterOrdersByStatusCommand.valueOf(status.name())
        );
        PaginatedResponse<OrderResponse> response = paginatedResponseMapper.from(paginatedResult,
                paginatedResult.content()
                        .stream()
                        .map(orderResponseMapper::toResponse)
                        .toList()
        );
        return ResponseEntity.ok(response);
    }
}
