package br.com.devpasso.order_management.domain.service;

import br.com.devpasso.order_management.application.dto.command.CreateProductCommand;
import br.com.devpasso.order_management.application.dto.command.UpdateProductCommand;
import br.com.devpasso.order_management.application.exception.ResourceConflictException;
import br.com.devpasso.order_management.application.service.ProductsService;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductsService productsService;

    @Test
    @DisplayName("Should return paginated products")
    void execute_ShouldReturnPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "");
        Pageable pageable = PageRequest.of(0, 10);
        String name = "Test";

        ProductEntity productEntity =
            new ProductEntity();
        productEntity.changeName("Test Product");


        Product domainProduct = new Product(productEntity.getId(),
                productEntity.getName(),
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now());

        PaginatedResult<Product> expectedResult = new PaginatedResult<>(
                List.of(domainProduct), 0, 10, 1, 1
        );

        when(productRepository.findAllByNameContainingIgnoreCase(paginationQuery, name))
                .thenReturn(expectedResult);

        // When
        PaginatedResult<Product> result = productsService.execute(paginationQuery, name);

        // Then
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(domainProduct, result.content()
                .getFirst());
        verify(productRepository).findAllByNameContainingIgnoreCase(paginationQuery, name);
    }

    @Test
    @DisplayName("Should return product by ID")
    void execute_WithId_ShouldReturnProduct() {
        // Given
        UUID id = UUID.randomUUID();
        Product domainProduct = new Product(id,
                "Test Product",
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now());

        when(productRepository.findById(id.toString()))
                .thenReturn(Optional.of(domainProduct));

        // When
        Product result = productsService.execute(id.toString());

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Test Product", result.getName());
        verify(productRepository).findById(id.toString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void execute_WithId_ShouldThrowExceptionWhenNotFound() {
        // Given
        String id = UUID.randomUUID()
                .toString();
        when(productRepository.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> productsService.execute(id));
        verify(productRepository).findById(id);
    }

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

        when(productRepository.existsByName(command.name())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // When
        Product result = productsService.execute(command);

        // Then
        assertNotNull(result);
        assertEquals(generatedId, result.getId());
        assertEquals(command.name(), result.getName());
        assertEquals(command.description(), result.getDescription());
        assertEquals(command.price(), result.getPrice());
        assertEquals(command.stockQuantity(), result.getStockQuantity());
        assertEquals(createdAt, result.getCreatedAt());

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
                () -> productsService.execute(command)
        );

        assertEquals("Product already exists with name: " + command.name(), exception.getMessage());
        verify(productRepository).existsByName(command.name());
        verify(productRepository, never()).save(any());
    }

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

        when(productRepository.findById(id.toString())).thenReturn(Optional.of(existingProduct));
        when(productRepository.existsByName(command.name())).thenReturn(false);
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Product result = productsService.execute(id.toString(), command);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(command.name(), result.getName());
        assertEquals(command.description(), result.getDescription());
        assertEquals(command.price(), result.getPrice());
        assertEquals(5, result.getStockQuantity());
        assertEquals(createdAt, result.getCreatedAt());

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
                () -> productsService.execute(id.toString(), command)
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

        when(productRepository.findById(id.toString())).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Product result = productsService.execute(id.toString(), command);

        // Then
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals("Original Name", result.getName());
        assertEquals(command.description(), result.getDescription());
        assertEquals(command.price(), result.getPrice());
        assertEquals(5, result.getStockQuantity());
        assertEquals(createdAt, result.getCreatedAt());

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
                () -> productsService.execute(id, command)
        );

        assertEquals("Product not found for ID: " + id, exception.getMessage());
        verify(productRepository).findById(id);
        verify(productRepository, never()).save(any());
    }
}
