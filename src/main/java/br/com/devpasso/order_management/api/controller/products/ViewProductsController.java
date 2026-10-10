package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.NotFoundErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.api.dto.response.ProductResponse;
import br.com.devpasso.order_management.api.mapper.response.PaginatedResponseMapper;
import br.com.devpasso.order_management.api.mapper.response.ProductResponseMapper;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.application.mapper.PaginationQueryMapper;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class ViewProductsController extends ProductsController {
    private final ProductResponseMapper productResponseMapper;
    private final ListProductsUseCase listProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final PaginationQueryMapper paginationQueryMapper;
    private final PaginatedResponseMapper paginatedResponseMapper;

    public ViewProductsController(ProductResponseMapper productResponseMapper,
                                  ListProductsUseCase listProductsUseCase,
                                  FindProductByIdUseCase findProductByIdUseCase,
                                  PaginationQueryMapper paginationQueryMapper,
                                  PaginatedResponseMapper paginatedResponseMapper) {
        super();
        this.productResponseMapper = productResponseMapper;
        this.listProductsUseCase = listProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.paginationQueryMapper = paginationQueryMapper;
        this.paginatedResponseMapper = paginatedResponseMapper;
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
}
