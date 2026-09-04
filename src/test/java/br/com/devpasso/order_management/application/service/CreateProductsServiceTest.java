package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.result.CreateProductResult;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.mapper.CreateProductResultMapper;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CreateProductsServiceTest {
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CreateProductResultMapper createProductResultMapper;
    
    @InjectMocks
    private CreateProductsService createProductsService;

    @Test
    @DisplayName("Should create and return product when name is unique")
    void execute_WithCreateProductCommand_ShouldSaveAndReturnProduct() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
                "Unique Product",
                "Description",
                new BigDecimal("99.99"),
                10
        );

        UUID generatedId = UUID.randomUUID();
        Instant createdAt = Instant.now();
        Product savedProduct = new Product(
                generatedId,
                command.name(),
                command.description(),
                command.price(),
                command.stockQuantity(),
                createdAt
        );

        CreateProductResult createdProductResult = new CreateProductResult(
                generatedId,
                command.name(),
                command.description(),
                command.price(),
                command.stockQuantity(),
                createdAt
        );

        when(productRepository.existsByName(command.name())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(createProductResultMapper.toResult(savedProduct)).thenReturn(createdProductResult);

        // When
        CreateProductResult result = createProductsService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(generatedId, result.id());
        assertEquals(command.name(), result.name());
        assertEquals(command.description(), result.description());
        assertEquals(command.price(), result.price());
        assertEquals(command.stockQuantity(), result.stockQuantity());

        verify(productRepository).existsByName(command.name());
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should throw ResourceConflictException when creating product with existing name")
    void execute_WithCreateProductCommand_ShouldThrowConflictExceptionWhenNameExists() {
        // Given
        CreateProductCommand command = new CreateProductCommand(
                "Existing Product",
                "Description",
                new BigDecimal("99.99"),
                10
        );

        when(productRepository.existsByName(command.name())).thenReturn(true);

        // When & Then
        ResourceConflictException exception = assertThrows(
                ResourceConflictException.class,
                () -> createProductsService.execute(command)
        );

        assertEquals("Product already exists with name: " + command.name(), exception.getMessage());
        verify(productRepository).existsByName(command.name());
        verify(productRepository, never()).save(any());
    }
}
