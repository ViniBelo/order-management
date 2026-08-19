package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.api.dto.CreateProductRequest;
import br.com.devpasso.order_management.api.mapper.ProductRequestMapper;
import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.response.CreateProductResponse;
import br.com.devpasso.order_management.application.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.application.dto.response.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ListProductsUseCase listProductsUseCase;

    @Mock
    private FindProductByIdUseCase findProductByIdUseCase;

    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private WebPaginationMapper paginationMapper;

    @Mock
    private ProductResponseMapper mapper;

    @Mock
    private ProductRequestMapper productRequestMapper;

    @InjectMocks
    private ProductsController productsController;

    @Test
    @DisplayName("Should return paginated products")
    void listAll_ShouldReturnOkWithPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Test";

        Product product = new Product(UUID.randomUUID(),
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5, Instant.now());

        PaginatedResult<Product> productPaginatedResult = new PaginatedResult<>(
                List.of(product), 0, 20, 1, 1
        );

        ProductResponse response = new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(productPaginatedResult);
        when(mapper.toResponse(product))
                .thenReturn(response);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = productsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK,
                result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().totalElements());
        assertEquals(response, result.getBody().content().getFirst());

        verify(listProductsUseCase).execute(paginationQuery, name);
        verify(mapper).toResponse(product);
    }

    @Test
    @DisplayName("Should return product by ID")
    void findById_ShouldReturnOkWithProduct() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(id,
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5, Instant.now());

        ProductResponse response = new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());

        when(findProductByIdUseCase.execute(id.toString()))
                .thenReturn(product);
        when(mapper.toResponse(product))
                .thenReturn(response);

        // When
        ResponseEntity<ProductResponse> result = productsController.findById(id);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(findProductByIdUseCase).execute(id.toString());
        verify(mapper).toResponse(product);
    }

    @Test
    @DisplayName("Should create product and return created status with location header")
    void createProduct_ShouldReturnCreatedWithLocationAndProductResponse() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "New Product",
                "Product description",
                new BigDecimal("49.99"),
                10
        );

        CreateProductCommand command = new CreateProductCommand(
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity()
        );

        UUID productId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Product createdProduct = new Product(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                createdAt
        );

        when(productRequestMapper.toCommand(request)).thenReturn(command);
        when(createProductUseCase.execute(command)).thenReturn(createdProduct);

        // When
        ResponseEntity<CreateProductResponse> result = productsController.createProduct(request);

        // Then
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(URI.create("/v1/products/" + productId), result.getHeaders().getLocation());
        assertNotNull(result.getBody());
        assertEquals(productId.toString(), result.getBody().id());
        assertEquals(request.name(), result.getBody().name());
        assertEquals(request.description(), result.getBody().description());
        assertEquals(request.price(), result.getBody().price());
        assertEquals(request.stockQuantity(), result.getBody().stockQuantity());
        assertEquals(createdAt.toString(), result.getBody().createdAt());

        verify(productRequestMapper).toCommand(request);
        verify(createProductUseCase).execute(command);
    }
}
