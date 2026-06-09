package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.application.dto.PaginatedResponse;
import br.com.devpasso.order_management.application.dto.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping( "/v1/products")
@Tag( name = "Products", description = "Operations about products" )
public class ProductsController {
    private final ListProductsUseCase listProductsUseCase;
    private final ProductResponseMapper mapper;

    public ProductsController(ListProductsUseCase listProductsUseCase,
                              ProductResponseMapper mapper) {
        this.listProductsUseCase = listProductsUseCase;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation( summary = "List all products" )
    public ResponseEntity<PaginatedResponse<ProductResponse>> listAll(
            @ParameterObject
            @PageableDefault(size = 20, sort = "name")
            Pageable pageable
    ) {
        Page<ProductResponse> products = listProductsUseCase.execute(pageable)
                .map(mapper::toResponse);
        PaginatedResponse<ProductResponse> response = PaginatedResponse.from(products, products.getContent());
        return ResponseEntity.ok(response);
    }
}
