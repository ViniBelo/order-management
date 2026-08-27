package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.api.dto.CreateProductRequest;
import br.com.devpasso.order_management.api.dto.UpdateProductRequest;
import br.com.devpasso.order_management.api.dto.UpdateProductStockRequest;
import br.com.devpasso.order_management.api.mapper.CreateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.UpdateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.UpdateProductStockRequestMapper;
import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.response.CreateProductResponse;
import br.com.devpasso.order_management.application.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.application.dto.response.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Operations about products")
public class ProductsController {
    private final ListProductsUseCase listProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final CreateProductUseCase createProductUseCase;
    private final UpdateProductUseCase updateProductUseCase;
    private final UpdateProductStockUseCase updateProductStockUseCase;
    private final WebPaginationMapper paginationMapper;
    private final ProductResponseMapper productResponseMapper;
    private final CreateProductRequestMapper createProductRequestMapper;
    private final UpdateProductRequestMapper updateProductRequestMapper;
    private final UpdateProductStockRequestMapper updateProductStockRequestMapper;

    public ProductsController(ListProductsUseCase listProductsUseCase,
                              FindProductByIdUseCase findProductByIdUseCase,
                              CreateProductUseCase createProductUseCase,
                              UpdateProductUseCase updateProductUseCase,
                              UpdateProductStockUseCase updateProductStockUseCase,
                              WebPaginationMapper paginationMapper,
                              ProductResponseMapper productResponseMapper,
                              CreateProductRequestMapper createProductRequestMapper,
                              UpdateProductRequestMapper updateProductRequestMapper,
                              UpdateProductStockRequestMapper updateProductStockRequestMapper) {
        this.listProductsUseCase = listProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.createProductUseCase = createProductUseCase;
        this.updateProductUseCase = updateProductUseCase;
        this.updateProductStockUseCase = updateProductStockUseCase;
        this.paginationMapper = paginationMapper;
        this.productResponseMapper = productResponseMapper;
        this.createProductRequestMapper = createProductRequestMapper;
        this.updateProductRequestMapper = updateProductRequestMapper;
        this.updateProductStockRequestMapper = updateProductStockRequestMapper;
    }

    @GetMapping
    @Operation(summary = "List all products")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "400", description = "Invalid pagination or sort parameter",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<PaginatedResponse<ProductResponse>> listAll(
            @ParameterObject
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "")
            String name
    ) {
        PaginatedResult<Product> products = listProductsUseCase.execute(
                paginationMapper.toDomainQuery(pageable),
                name
        );
        List<ProductResponse> productsResponse = products.content()
                .stream()
                .map(productResponseMapper::toResponse)
                .toList();
        PaginatedResponse<ProductResponse> response =
                PaginatedResponse.from(products, productsResponse);
        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{id}")
    @Operation(summary = "Find product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success"),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProductResponse> findById(
            @PathVariable UUID id
    ) {
        Product product = findProductByIdUseCase.execute(id.toString());
        ProductResponse response = productResponseMapper.toResponse(product);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "409", description = "Product already exists",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<CreateProductResponse> createProduct(
            @RequestBody
            @Valid
            CreateProductRequest createProductRequest
    ) {
        CreateProductCommand productToCreate = createProductRequestMapper.toCommand(createProductRequest);
        CreateProductResponse createdProduct = CreateProductResponse.build(createProductUseCase.execute(productToCreate));
        return ResponseEntity.created(URI.create("/v1/products/" + createdProduct.id()))
                .body(createdProduct);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates products fields, except stock")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @RequestBody
            @Valid
            UpdateProductRequest updateProductRequest
    ) {
        UpdateProductCommand updateProductCommand = updateProductRequestMapper.toCommand(updateProductRequest);
        ProductResponse updatedProduct = productResponseMapper.toResponse(updateProductUseCase.execute(id.toString(),
                updateProductCommand));
        return ResponseEntity.ok(updatedProduct);
    }

    @PatchMapping("/{id}/stock")
    @Operation(summary = "Update product stock quantity")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<ProductResponse> updateStock(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProductStockRequest updateProductStockRequest
    ) {
        UpdateProductStockCommand command = updateProductStockRequestMapper.toCommand(updateProductStockRequest);
        ProductResponse updatedProduct = productResponseMapper.toResponse(updateProductStockUseCase.execute(id.toString(),
                command));
        return ResponseEntity.ok(updatedProduct);
    }
}
