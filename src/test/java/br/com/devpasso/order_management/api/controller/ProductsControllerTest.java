package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.api.dto.CreateProductRequest;
import br.com.devpasso.order_management.api.dto.UpdateProductRequest;
import br.com.devpasso.order_management.api.mapper.CreateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.UpdateProductRequestMapper;
import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.response.CreateProductResponse;
import br.com.devpasso.order_management.application.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.application.dto.response.ProductResponse;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
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

import static org.junit.jupiter.api.Assertions.*;
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
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private WebPaginationMapper paginationMapper;

    @Mock
    private ProductResponseMapper mapper;

    @Mock
    private CreateProductRequestMapper createProductRequestMapper;

    @Mock
    private UpdateProductRequestMapper updateProductRequestMapper;

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
    @DisplayName("Should return empty paginated response when no products found")
    void listAll_ShouldReturnOkWithEmptyPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Nonexistent";

        PaginatedResult<Product> emptyPaginatedResult = new PaginatedResult<>(
                List.of(), 0, 20, 0, 0
        );

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(emptyPaginatedResult);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = productsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().totalElements());
        assertTrue(result.getBody().content().isEmpty());

        verify(paginationMapper).toDomainQuery(pageable);
        verify(listProductsUseCase).execute(paginationQuery, name);
        verifyNoInteractions(mapper);
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
    @DisplayName("Should throw ResourceNotFoundException when product not found by ID")
    void findById_ShouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        when(findProductByIdUseCase.execute(id.toString()))
                .thenThrow(new ResourceNotFoundException("Product not found for ID: " + id));

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productsController.findById(id)
        );

        assertEquals("Product not found for ID: " + id, exception.getMessage());
        verify(findProductByIdUseCase).execute(id.toString());
        verifyNoInteractions(mapper);
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

        when(createProductRequestMapper.toCommand(request)).thenReturn(command);
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

        verify(createProductRequestMapper).toCommand(request);
        verify(createProductUseCase).execute(command);
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when creating product with duplicate name")
    void createProduct_ShouldThrowResourceConflictExceptionWhenNameAlreadyExists() {
        // Given
        CreateProductRequest request = new CreateProductRequest(
                "Duplicate Product",
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

        when(createProductRequestMapper.toCommand(request)).thenReturn(command);
        when(createProductUseCase.execute(command))
                .thenThrow(new ResourceConflictException("Product already exists with name: " + request.name()));

        // When & Then
        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> productsController.createProduct(request)
        );

        assertEquals("Product already exists with name: " + request.name(), exception.getMessage());
        verify(createProductRequestMapper).toCommand(request);
        verify(createProductUseCase).execute(command);
    }

    @Test
    @DisplayName("Should update product and return ok status with product response")
    void updateProduct_ShouldReturnOkWithProductResponse() {
        // Given
        UUID productId = UUID.randomUUID();
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Name",
                "Updated Description",
                new BigDecimal("59.99")
        );

        UpdateProductCommand command = new UpdateProductCommand(
                request.name(),
                request.description(),
                request.price()
        );

        Instant createdAt = Instant.now();
        Product updatedProduct = new Product(
                productId,
                request.name(),
                request.description(),
                request.price(),
                10,
                createdAt
        );

        ProductResponse response = new ProductResponse(
                productId,
                request.name(),
                request.description(),
                request.price(),
                10
        );

        when(updateProductRequestMapper.toCommand(request)).thenReturn(command);
        when(updateProductUseCase.execute(productId.toString(), command)).thenReturn(updatedProduct);
        when(mapper.toResponse(updatedProduct)).thenReturn(response);

        // When
        ResponseEntity<ProductResponse> result = productsController.updateProduct(productId, request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(updateProductRequestMapper).toCommand(request);
        verify(updateProductUseCase).execute(productId.toString(), command);
        verify(mapper).toResponse(updatedProduct);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent product")
    void updateProduct_ShouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        UpdateProductRequest request = new UpdateProductRequest(
                "Updated Name",
                "Updated Description",
                new BigDecimal("59.99")
        );

        UpdateProductCommand command = new UpdateProductCommand(
                request.name(),
                request.description(),
                request.price()
        );

        when(updateProductRequestMapper.toCommand(request)).thenReturn(command);
        when(updateProductUseCase.execute(productId.toString(), command))
                .thenThrow(new ResourceNotFoundException("Product not found for ID: " + productId));

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productsController.updateProduct(productId, request)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(updateProductRequestMapper).toCommand(request);
        verify(updateProductUseCase).execute(productId.toString(), command);
        verifyNoInteractions(mapper);
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when updating product with duplicate name")
    void updateProduct_ShouldThrowResourceConflictExceptionWhenNameAlreadyExists() {
        // Given
        UUID productId = UUID.randomUUID();
        UpdateProductRequest request = new UpdateProductRequest(
                "Existing Name",
                "Updated Description",
                new BigDecimal("59.99")
        );

        UpdateProductCommand command = new UpdateProductCommand(
                request.name(),
                request.description(),
                request.price()
        );

        when(updateProductRequestMapper.toCommand(request)).thenReturn(command);
        when(updateProductUseCase.execute(productId.toString(), command))
                .thenThrow(new ResourceConflictException("Product already exists with name: " + request.name()));

        // When & Then
        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> productsController.updateProduct(productId, request)
        );

        assertEquals("Product already exists with name: " + request.name(), exception.getMessage());
        verify(updateProductRequestMapper).toCommand(request);
        verify(updateProductUseCase).execute(productId.toString(), command);
        verifyNoInteractions(mapper);
    }
}
