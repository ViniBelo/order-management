package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.application.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.application.dto.response.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Operations about products")
public class ProductsController {
    private final ListProductsUseCase listProductsUseCase;
    private final FindProductByIdUseCase findProductByIdUseCase;
    private final WebPaginationMapper paginationMapper;
    private final ProductResponseMapper mapper;

    public ProductsController(ListProductsUseCase listProductsUseCase,
                              FindProductByIdUseCase findProductByIdUseCase,
                              WebPaginationMapper paginationMapper,
                              ProductResponseMapper mapper) {
        this.listProductsUseCase = listProductsUseCase;
        this.findProductByIdUseCase = findProductByIdUseCase;
        this.paginationMapper = paginationMapper;
        this.mapper = mapper;
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
                .map(mapper::toResponse)
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
        ProductResponse response = mapper.toResponse(product);
        return ResponseEntity.ok(response);
    }
}
