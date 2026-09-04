package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.api.dto.exception.ConflictErrorResponse;
import br.com.devpasso.order_management.api.dto.request.CreateProductRequest;
import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.request.UpdateProductRequest;
import br.com.devpasso.order_management.api.dto.request.UpdateProductStockRequest;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.response.CreateProductResponse;
import br.com.devpasso.order_management.api.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.api.dto.response.ProductResponse;
import br.com.devpasso.order_management.api.mapper.request.CreateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.request.UpdateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.request.UpdateProductStockRequestMapper;
import br.com.devpasso.order_management.api.mapper.response.CreateProductResponseMapper;
import br.com.devpasso.order_management.api.mapper.response.PaginatedResponseMapper;
import br.com.devpasso.order_management.api.mapper.response.ProductResponseMapper;
import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.CreateProductResult;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.application.mapper.PaginationQueryMapper;
import br.com.devpasso.order_management.application.usecase.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Operations about products")
public class ProductsController {
    private final ListProductsUseCase listProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final DeleteProductUseCase deleteProductUseCase;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginatedResponseMapper paginatedResponseMapper;
    private final ProductResponseMapper productResponseMapper;
    private final CreateProductRequestMapper createProductRequestMapper;
    private final CreateProductResponseMapper createProductResponseMapper;
    private final UpdateProductRequestMapper updateProductRequestMapper;
    private final UpdateProductStockRequestMapper updateProductStockRequestMapper;

    public ProductsController(ListProductsUseCase listProductsUseCase,
                              FindProductByIdUseCase findProductByIdUseCase,
                              CreateProductUseCase createProductUseCase,
                              UpdateProductUseCase updateProductUseCase,
                              UpdateProductStockUseCase updateProductStockUseCase,
                              DeleteProductUseCase deleteProductUseCase,
                              PaginationQueryMapper paginationQueryMapper,
                              PaginatedResponseMapper paginatedResponseMapper,
                              ProductResponseMapper productResponseMapper,
                              CreateProductRequestMapper createProductRequestMapper,
                              CreateProductResponseMapper createProductResponseMapper,
                              UpdateProductRequestMapper updateProductRequestMapper,
                              UpdateProductStockRequestMapper updateProductStockRequestMapper) {
        this.listProductsUseCase = listProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.deleteProductUseCase = deleteProductUseCase;
        this.paginationQueryMapper = paginationQueryMapper;
        this.paginatedResponseMapper = paginatedResponseMapper;
        this.productResponseMapper = productResponseMapper;
        this.createProductRequestMapper = createProductRequestMapper;
        this.createProductResponseMapper = createProductResponseMapper;
        this.updateProductRequestMapper = updateProductRequestMapper;
        this.updateProductStockRequestMapper = updateProductStockRequestMapper;
    }

    @GetMapping
    @Operation(summary = "List all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameter provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<PaginatedResponse<ProductResponse>> listAll(
            @ParameterObject
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable,
            @Parameter(description = "Filter products by name (case-insensitive)", example = "Smartphone")
            @RequestParam(required = false, defaultValue = "")
            String name
    ) {
        PaginatedResult<ProductResult> paginatedResult = listProductsUseCase.execute(
                paginationQueryMapper.toDomainQuery(pageable),
                name
        );
        PaginatedResponse<ProductResponse> response = paginatedResponseMapper.from(paginatedResult,
                paginatedResult.content()
                        .stream()
                        .map(productResponseMapper::toResponse)
                        .collect(Collectors.toList())
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Find product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<ProductResponse> findById(
            @Parameter(description = "Product UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id
    ) {
        ProductResult result = findProductByIdUseCase.execute(id.toString());
        return ResponseEntity.ok(productResponseMapper.toResponse(result));
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data or value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Product already exists",
                    content = @Content(schema = @Schema(implementation = ConflictErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<CreateProductResponse> createProduct(
            @RequestBody
            @Valid
            CreateProductRequest createProductRequest
    ) {
        CreateProductCommand productToCreate = createProductRequestMapper.toCommand(createProductRequest);
        CreateProductResult createdProductResult = createProductUseCase.execute(productToCreate);
        CreateProductResponse createdProduct = createProductResponseMapper.toResponse(createdProductResult);
        return ResponseEntity.created(URI.create("/v1/products/" + createdProduct.id()))
                .body(createdProduct);
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

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid value provided",
                    content = @Content(schema = @Schema(implementation = ValidationErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = NotFoundErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = InternalServerErrorResponse.class)))
    })
    public ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product UUID", example = "123e4567-e89b-12d3-a456-426614174000")
            @PathVariable UUID id
    ) {
        deleteProductUseCase.execute(id.toString());
        return ResponseEntity.noContent()
                .build();
    }
}
