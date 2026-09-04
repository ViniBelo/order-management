package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.ProductResult;
import br.com.devpasso.order_management.application.mapper.ProductResultMapper;
import br.com.devpasso.order_management.domain.common.PaginatedQueryResult;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductResultMapper productResultMapper;

    @InjectMocks
    private FindProductsService findProductsService;

    @Test
    @DisplayName("Should return paginated products")
    void execute_ShouldReturnPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "");
        String name = "Test";

        ProductEntity productEntity =
            new ProductEntity();
        productEntity.changeName("Test Product");

        Product domainProduct = new Product(productEntity.getId(),
                productEntity.getName(),
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now()
        );

        ProductResult productResult = new ProductResult(productEntity.getId(),
                productEntity.getName(),
                "Test description",
                new BigDecimal("10.0"),
                5);

        PaginatedQueryResult<Product> expectedResult = new PaginatedQueryResult<>(
                List.of(domainProduct), 0, 10, 1, 1
        );

        List<ProductResult> productsResultList = List.of(productResult);

        PaginatedResult<ProductResult> paginatedResult = new PaginatedResult<>(
                productsResultList, 0, 10, 1, 1
        );

        when(productRepository.findAllByNameContainingIgnoreCase(paginationQuery, name))
                .thenReturn(expectedResult);
        when(productResultMapper.toResult(domainProduct))
                .thenReturn(productResult);

        // When
        PaginatedResult<ProductResult> result = findProductsService.execute(paginationQuery, name);

        // Then
        assertNotNull(result);
        assertEquals(1, result.totalElements());
        assertEquals(productResult, result.content()
                .getFirst());
        verify(productRepository).findAllByNameContainingIgnoreCase(paginationQuery, name);
    }

    @Test
    @DisplayName("Should return product by ID")
    void execute_WithId_ShouldReturnProduct() {
        // Given
        UUID id = UUID.randomUUID();

        Product domainProduct = new Product(
                id,
                "Test Product",
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now()
        );

        ProductResult productResult = new ProductResult(
                id,
                "Test Product",
                "Test description",
                new BigDecimal("10.0"),
                5
        );

        when(productRepository.findById(id.toString()))
                .thenReturn(Optional.of(domainProduct));
        when(productResultMapper.toResult(domainProduct))
                .thenReturn(productResult);

        // When
        ProductResult result = findProductsService.execute(id.toString());

        // Then
        assertNotNull(result);
        assertEquals(id, result.id());
        assertEquals("Test Product", result.name());
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
        assertThrows(ResourceNotFoundException.class, () -> findProductsService.execute(id));
        verify(productRepository).findById(id);
    }
}
