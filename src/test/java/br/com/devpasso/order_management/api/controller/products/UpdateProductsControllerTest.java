package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.api.dto.products.request.UpdateProductRequest;
import br.com.devpasso.order_management.api.dto.products.request.UpdateProductStockRequest;
import br.com.devpasso.order_management.api.dto.products.response.ProductResponse;
import br.com.devpasso.order_management.api.mapper.products.request.UpdateProductRequestMapper;
import br.com.devpasso.order_management.api.mapper.products.request.UpdateProductStockRequestMapper;
import br.com.devpasso.order_management.api.mapper.products.response.ProductResponseMapper;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.products.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceConflictException;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductsControllerTest {
    @Mock
    private ProductResponseMapper productResponseMapper;

    @Mock
    private UpdateProductUseCase updateProductUseCase;

    @Mock
    private UpdateProductStockUseCase updateProductStockUseCase;

    @Mock
    private UpdateProductRequestMapper updateProductRequestMapper;

    @Mock
    private UpdateProductStockRequestMapper updateProductStockRequestMapper;

    @InjectMocks
    private UpdateProductsController updateProductsController;

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
        ResponseEntity<ProductResponse> result = updateProductsController.updateProduct(productId, request);

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
                () -> updateProductsController.updateProduct(productId, request)
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
                () -> updateProductsController.updateProduct(productId, request)
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
        ResponseEntity<ProductResponse> result = updateProductsController.updateStock(productId, request);

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
                () -> updateProductsController.updateStock(productId, request)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(updateProductStockRequestMapper).toCommand(request);
        verify(updateProductStockUseCase).execute(productId.toString(), command);
        verifyNoInteractions(productResponseMapper);
    }
}
