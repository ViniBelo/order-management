package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductStockCommand;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.ProductResultMapper;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UpdateProductsServiceTest {
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductResultMapper productResultMapper;
    
    @InjectMocks
    private UpdateProductsService updateProductsService;
    
    @Test
    @DisplayName("Should update and return product when product exists")
    void execute_WithUpdateProductCommand_ShouldUpdateAndReturnProduct() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Product existingProduct = new Product(
                id,
                "Original Name",
                "Original Description",
                new BigDecimal("29.99"),
                5,
                createdAt
        );

        UpdateProductCommand command = new UpdateProductCommand(
                "Updated Name",
                "Updated Description",
                new BigDecimal("49.99")
        );

        ProductResult productResult = new ProductResult(
                id,
                command.name(),
                command.description(),
                command.price(),
                existingProduct.getStockQuantity()
        );

        when(productRepository.findById(id.toString()))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName(command.name()))
                .thenReturn(false);
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(productResultMapper.toResult(existingProduct))
                .thenReturn(productResult);

        // When
        ProductResult result = updateProductsService.execute(id.toString(), command);

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals(command.name(), result.name());
        assertEquals(command.description(), result.description());
        assertEquals(command.price(), result.price());
        assertEquals(5, result.stockQuantity());

        verify(productRepository).findById(id.toString());
        verify(productRepository).existsByName(command.name());
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when updating product with existing name")
    void execute_WithUpdateProductCommand_ShouldThrowConflictExceptionWhenNameAlreadyExists() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Product existingProduct = new Product(
                id,
                "Original Name",
                "Original Description",
                new BigDecimal("29.99"),
                5,
                createdAt
        );

        UpdateProductCommand command = new UpdateProductCommand(
                "Existing Product Name",
                "Updated Description",
                new BigDecimal("49.99")
        );

        when(productRepository.findById(id.toString())).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName(command.name())).thenReturn(true);

        // When & Then
        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> updateProductsService.execute(id.toString(), command)
        );

        assertEquals("Product already exists with name: " + command.name(), exception.getMessage());
        verify(productRepository).findById(id.toString());
        verify(productRepository).existsByName(command.name());
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should not validate duplicate name when product name is unchanged")
    void execute_WithUpdateProductCommand_ShouldNotValidateDuplicateNameWhenNameIsUnchanged() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Product existingProduct = new Product(
                id,
                "Original Name",
                "Original Description",
                new BigDecimal("29.99"),
                5,
                createdAt
        );

        UpdateProductCommand command = new UpdateProductCommand(
                "Original Name",
                "Updated Description",
                new BigDecimal("49.99")
        );

        ProductResult productResult = new ProductResult(
                id,
                command.name(),
                command.description(),
                command.price(),
                existingProduct.getStockQuantity()
        );

        when(productRepository.findById(id.toString()))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(productResultMapper.toResult(existingProduct))
                .thenReturn(productResult);

        // When
        ProductResult result = updateProductsService.execute(id.toString(), command);

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Original Name", result.name());
        assertEquals(command.description(), result.description());
        assertEquals(command.price(), result.price());
        assertEquals(5, result.stockQuantity());

        verify(productRepository).findById(id.toString());
        verify(productRepository, never()).existsByName(any());
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating non-existent product")
    void execute_WithUpdateProductCommand_ShouldThrowExceptionWhenNotFound() {
        // Given
        String id = UUID.randomUUID().toString();
        UpdateProductCommand command = new UpdateProductCommand(
                "Updated Name",
                "Updated Description",
                new BigDecimal("49.99")
        );

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateProductsService.execute(id, command)
        );

        assertEquals("Product not found for ID: " + id, exception.getMessage());
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void execute_WithUpdateProductStockCommand_ShouldUpdateStockSuccessfully() {
        // Given
        UUID id = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Product existingProduct = new Product(
                id,
                "Test Product",
                "Test Description",
                new BigDecimal("29.99"),
                5,
                createdAt
        );

        UpdateProductStockCommand command = new UpdateProductStockCommand(50);

        ProductResult productResult = new ProductResult(
                id,
                existingProduct.getName(),
                existingProduct.getDescription(),
                existingProduct.getPrice(),
                command.stockQuantity()
        );

        when(productRepository.findById(id.toString()))
                .thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(productResultMapper.toResult(existingProduct))
                .thenReturn(productResult);

        // When
        ProductResult result = updateProductsService.execute(id.toString(), command);

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Test Product", result.name());
        assertEquals("Test Description", result.description());
        assertEquals(new BigDecimal("29.99"), result.price());
        assertEquals(50, result.stockQuantity());

        verify(productRepository).findById(id.toString());
        verify(productRepository).save(existingProduct);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when updating stock of non-existent product")
    void execute_WithUpdateProductStockCommand_ShouldThrowExceptionWhenNotFound() {
        // Given
        String id = UUID.randomUUID().toString();
        UpdateProductStockCommand command = new UpdateProductStockCommand(50);

        when(productRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> updateProductsService.execute(id, command)
        );

        assertEquals("Product not found for ID: " + id, exception.getMessage());
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any());
    }
}
