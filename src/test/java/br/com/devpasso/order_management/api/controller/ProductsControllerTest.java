package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.api.dto.request.CreateProductRequest;
import br.com.devpasso.order_management.api.dto.request.UpdateProductRequest;
import br.com.devpasso.order_management.api.dto.request.UpdateProductStockRequest;
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
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.PaginationQueryMapper;
import br.com.devpasso.order_management.application.usecase.*;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
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
    private UpdateProductStockUseCase updateProductStockUseCase;

    @Mock
    private DeleteProductUseCase deleteProductUseCase;

    @Mock
    private PaginationQueryMapper paginationMapper;

    @Mock
    private PaginatedResponseMapper paginatedResponseMapper;

    @Mock
    private CreateProductResponseMapper createProductResponseMapper;

    @Mock
    private ProductResponseMapper productResponseMapper;

    @Mock
    private CreateProductRequestMapper createProductRequestMapper;

    @Mock
    private UpdateProductRequestMapper updateProductRequestMapper;

    @Mock
    private UpdateProductStockRequestMapper updateProductStockRequestMapper;

    @InjectMocks
    private ProductsController productsController;

    @Test
    @DisplayName("Should return paginated products")
    void listAll_ShouldReturnOkWithPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Test";

        ProductResult product = new ProductResult(UUID.randomUUID(),
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5);

        PaginatedResult<ProductResult> productPaginatedResult = new PaginatedResult<>(
                List.of(product), 0, 20, 1, 1
        );

        ProductResponse productResponse = new ProductResponse(product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity());

        List<ProductResponse> productResponses = List.of(productResponse);
        PaginatedResponse<ProductResponse> paginatedResponse = new PaginatedResponse<>(
                productResponses,
                0,
                20,
                1,
                1
        );

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(productPaginatedResult);
        when(productResponseMapper.toResponse(product))
                .thenReturn(productResponse);
        when(paginatedResponseMapper.from(productPaginatedResult, productResponses))
                .thenReturn(paginatedResponse);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = productsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK,
                result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().totalElements());
        assertEquals(productResponse, result.getBody().content().getFirst());

        verify(listProductsUseCase).execute(paginationQuery, name);
        verify(productResponseMapper).toResponse(product);
    }

    @Test
    @DisplayName("Should return empty paginated response when no products found")
    void listAll_ShouldReturnOkWithEmptyPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Nonexistent";

        List<ProductResult> emptyProductResultsList = List.of();
        PaginatedResult<ProductResult> emptyPaginatedResult = new PaginatedResult<>(
                emptyProductResultsList, 0, 20, 0, 0
        );

        List<ProductResponse> emptyProductResponsesList = List.of();
        PaginatedResponse<ProductResponse> emptyPaginatedResponse = new PaginatedResponse<>(
                emptyProductResponsesList, 0, 20, 0, 0
        );

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(emptyPaginatedResult);
        when(paginatedResponseMapper.from(emptyPaginatedResult, emptyProductResponsesList))
                .thenReturn(emptyPaginatedResponse);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = productsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().totalElements());
        assertTrue(result.getBody().content().isEmpty());

        verify(paginationMapper).toDomainQuery(pageable);
        verify(listProductsUseCase).execute(paginationQuery, name);
        verifyNoInteractions(productResponseMapper);
    }

    @Test
    @DisplayName("Should return product by ID")
    void findById_ShouldReturnOkWithProduct() {
        // Given
        UUID id = UUID.randomUUID();
        ProductResult product = new ProductResult(id,
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5);

        ProductResponse response = new ProductResponse(product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity());

        when(findProductByIdUseCase.execute(id.toString()))
                .thenReturn(product);
        when(productResponseMapper.toResponse(product))
                .thenReturn(response);

        // When
        ResponseEntity<ProductResponse> result = productsController.findById(id);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(findProductByIdUseCase).execute(id.toString());
        verify(productResponseMapper).toResponse(product);
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
        verifyNoInteractions(productResponseMapper);
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
        CreateProductResult createdProductResult = new CreateProductResult(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                Instant.now()
        );

        CreateProductResponse createProductResponse = new CreateProductResponse(
                productId,
                request.name(),
                request.description(),
                request.price(),
                request.stockQuantity(),
                createdAt
        );

        when(createProductRequestMapper.toCommand(request)).thenReturn(command);
        when(createProductUseCase.execute(command)).thenReturn(createdProductResult);
        when(createProductResponseMapper.toResponse(createdProductResult)).thenReturn(createProductResponse);

        // When
        ResponseEntity<CreateProductResponse> response = productsController.createProduct(request);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(URI.create("/v1/products/" + productId), response.getHeaders().getLocation());
        assertNotNull(response.getBody());
        assertEquals(productId, response.getBody().id());
        assertEquals(request.name(), response.getBody().name());
        assertEquals(request.description(), response.getBody().description());
        assertEquals(request.price(), response.getBody().price());
        assertEquals(request.stockQuantity(), response.getBody().stockQuantity());
        assertEquals(createdAt, response.getBody().createdAt());

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

        ProductResult updatedProductResult = new ProductResult(
                productId,
                request.name(),
                request.description(),
                request.price(),
                10
        );

        ProductResponse response = new ProductResponse(
                productId,
                request.name(),
                request.description(),
                request.price(),
                10
        );

        when(updateProductRequestMapper.toCommand(request)).thenReturn(command);
        when(updateProductUseCase.execute(productId.toString(), command)).thenReturn(updatedProductResult);
        when(productResponseMapper.toResponse(updatedProductResult)).thenReturn(response);

        // When
        ResponseEntity<ProductResponse> result = productsController.updateProduct(productId, request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(updateProductRequestMapper).toCommand(request);
        verify(updateProductUseCase).execute(productId.toString(), command);
        verify(productResponseMapper).toResponse(updatedProductResult);
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
        verifyNoInteractions(productResponseMapper);
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
        verifyNoInteractions(productResponseMapper);
    }

    @Test
    @DisplayName("Should update product stock and return ok status with product response")
    void updateStock_ShouldReturnOkWithProductResponse() {
        // Given
        UUID productId = UUID.randomUUID();
        UpdateProductStockRequest request = new UpdateProductStockRequest(50);
        UpdateProductStockCommand command = new UpdateProductStockCommand(50);

        ProductResult updatedProductResult = new ProductResult(
                productId,
                "Test Product",
                "Test Description",
                new BigDecimal("29.99"),
                50
        );

        ProductResponse response = new ProductResponse(
                productId,
                "Test Product",
                "Test Description",
                new BigDecimal("29.99"),
                50
        );

        when(updateProductStockRequestMapper.toCommand(request)).thenReturn(command);
        when(updateProductStockUseCase.execute(productId.toString(), command)).thenReturn(updatedProductResult);
        when(productResponseMapper.toResponse(updatedProductResult)).thenReturn(response);

        // When
        ResponseEntity<ProductResponse> result = productsController.updateStock(productId, request);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(updateProductStockRequestMapper).toCommand(request);
        verify(updateProductStockUseCase).execute(productId.toString(), command);
        verify(productResponseMapper).toResponse(updatedProductResult);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating stock of non-existent product")
    void updateStock_ShouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        UpdateProductStockRequest request = new UpdateProductStockRequest(50);
        UpdateProductStockCommand command = new UpdateProductStockCommand(50);

        when(updateProductStockRequestMapper.toCommand(request)).thenReturn(command);
        when(updateProductStockUseCase.execute(productId.toString(), command))
                .thenThrow(new ResourceNotFoundException("Product not found for ID: " + productId));

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productsController.updateStock(productId, request)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(updateProductStockRequestMapper).toCommand(request);
        verify(updateProductStockUseCase).execute(productId.toString(), command);
        verifyNoInteractions(productResponseMapper);
    }

    @Test
    @DisplayName("Should delete product and return no content status")
    void deleteProduct_ShouldReturnNoContent() {
        // Given
        UUID productId = UUID.randomUUID();

        // When
        ResponseEntity<Void> result = productsController.deleteProduct(productId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deleteProductUseCase).execute(productId.toString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void deleteProduct_ShouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Product not found for ID: " + productId))
                .when(deleteProductUseCase).execute(productId.toString());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productsController.deleteProduct(productId)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(deleteProductUseCase).execute(productId.toString());
    }
}
