package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.api.dto.products.request.CreateProductRequest;
import br.com.devpasso.order_management.api.dto.products.response.CreateProductResponse;
import br.com.devpasso.order_management.api.mapper.request.CreateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.response.CreateProductResponseMapper;
import br.com.devpasso.order_management.application.dto.command.products.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.products.CreateProductResult;
import br.com.devpasso.order_management.application.usecase.products.CreateProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceConflictException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateProductsControllerTest {
    @Mock
    private CreateProductUseCase createProductUseCase;

    @Mock
    private CreateProductResponseMapper createProductResponseMapper;

    @Mock
    private CreateProductRequestMapper createProductRequestMapper;

    @InjectMocks
    private CreateProductsController createProductsController;

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
        ResponseEntity<CreateProductResponse> response = createProductsController.createProduct(request);

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
                () -> createProductsController.createProduct(request)
        );

        assertEquals("Product already exists with name: " + request.name(), exception.getMessage());
        verify(createProductRequestMapper).toCommand(request);
        verify(createProductUseCase).execute(command);
    }
}
