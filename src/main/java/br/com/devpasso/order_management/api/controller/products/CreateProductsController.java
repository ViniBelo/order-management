package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.api.dto.exception.ConflictErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.InternalServerErrorResponse;
import br.com.devpasso.order_management.api.dto.exception.ValidationErrorResponse;
import br.com.devpasso.order_management.api.dto.request.CreateProductRequest;
import br.com.devpasso.order_management.api.dto.response.CreateProductResponse;
import br.com.devpasso.order_management.api.mapper.request.CreateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.response.CreateProductResponseMapper;
import br.com.devpasso.order_management.application.dto.command.products.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.products.CreateProductResult;
import br.com.devpasso.order_management.application.usecase.products.CreateProductUseCase;
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
public class CreateProductsController extends ProductsController {
    private final CreateProductUseCase createProductUseCase;
    private final CreateProductRequestMapper createProductRequestMapper;
    private final CreateProductResponseMapper createProductResponseMapper;

    public CreateProductsController(CreateProductUseCase createProductUseCase,
                                    CreateProductRequestMapper createProductRequestMapper,
                                    CreateProductResponseMapper createProductResponseMapper) {
        super();
        this.createProductUseCase = createProductUseCase;
        this.createProductRequestMapper = createProductRequestMapper;
        this.createProductResponseMapper = createProductResponseMapper;
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
}
