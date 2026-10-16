package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.api.dto.exception.ConflictErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.request.UpdateProductRequest;
import br.com.devpasso.order_management.api.dto.request.UpdateProductStockRequest;
import br.com.devpasso.order_management.api.dto.response.ProductResponse;
import br.com.devpasso.order_management.api.mapper.request.UpdateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.request.UpdateProductStockRequestMapper;
import br.com.devpasso.order_management.api.mapper.response.ProductResponseMapper;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UpdateProductsController extends ProductsController {
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final ProductResponseMapper productResponseMapper;
    private final UpdateProductRequestMapper updateProductRequestMapper;
    private final UpdateProductStockRequestMapper updateProductStockRequestMapper;

    public UpdateProductsController(UpdateProductUseCase updateProductUseCase,
                                    UpdateProductStockUseCase updateProductStockUseCase,
                                    ProductResponseMapper productResponseMapper,
                                    UpdateProductRequestMapper updateProductRequestMapper,
                                    UpdateProductStockRequestMapper updateProductStockRequestMapper) {
        super();
        this.updateProductUseCase = updateProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.productResponseMapper = productResponseMapper;
        this.updateProductRequestMapper = updateProductRequestMapper;
        this.updateProductStockRequestMapper = updateProductStockRequestMapper;
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates products fields, except stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Product already exists",
                    content = @Content(schema = @Schema(implementation = ConflictErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @Parameter(description = "Product UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @RequestBody
            @Valid
            UpdateProductRequest updateProductRequest
    ) {
        UpdateProductCommand updateProductCommand = updateProductRequestMapper.toCommand(updateProductRequest);
        ProductResult updatedProductResult = updateProductUseCase.execute(id.toString(), updateProductCommand);
        ProductResponse updatedProduct = productResponseMapper.toResponse(updatedProductResult);
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> updateStock(
            @Parameter(description = "Product UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProductStockRequest updateProductStockRequest
    ) {
        UpdateProductStockCommand command = updateProductStockRequestMapper.toCommand(updateProductStockRequest);
        ProductResult updatedProduct = updateProductStockUseCase.execute(id.toString(), command);
        ProductResponse updatedProductResponse = productResponseMapper.toResponse(updatedProduct);
        return ResponseEntity.ok(updatedProductResponse);
    }
}
