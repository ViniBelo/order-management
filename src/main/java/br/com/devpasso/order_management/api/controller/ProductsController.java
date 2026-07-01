package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.application.dto.PaginatedResponse;
import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.application.dto.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.model.Product;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Operations about products")
public class ProductsController {
    private final ListProductsUseCase listProductsUseCase;
    private final WebPaginationMapper paginationMapper;
    private final ProductResponseMapper mapper;

    public ProductsController(ListProductsUseCase listProductsUseCase,
                              WebPaginationMapper paginationMapper,
                              ProductResponseMapper mapper) {
        this.listProductsUseCase = listProductsUseCase;
        this.paginationMapper = paginationMapper;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation( summary = "List all products" )
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
}
