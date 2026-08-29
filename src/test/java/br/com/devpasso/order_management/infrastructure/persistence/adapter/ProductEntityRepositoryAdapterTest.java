package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductEntityRepositoryAdapterTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @Mock
    private InfraPaginationMapper paginationMapper;

    @Mock
    private ProductModelMapper mapper;

    @Mock
    private ProductEntityMapper productEntityMapper;

    @InjectMocks
    private ProductRepositoryAdapter productRepositoryAdapter;

    @Test
    @DisplayName("Should delegate to JPA repository for findAllByName")
    void findAllByNameContainingIgnoreCase_ShouldDelegateToJpaRepository() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 10, "name,ASC");
        Pageable pageable = PageRequest.of(0, 10);
        String name = "test";

        ProductEntity productEntity = new ProductEntity();
        Page<ProductEntity> entityPage = new PageImpl<>(List.of(productEntity));

        Product domainProduct = new Product(
                UUID.randomUUID(),
                "Test product",
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now()
        );

        PaginatedResult<Product> expectedResult = new PaginatedResult<>(
                List.of(domainProduct), 0, 10, 1, 1
        );

        when(paginationMapper.toSpringPageable(paginationQuery))
                .thenReturn(pageable);
        when(productJpaRepository.findAllByNameContainingIgnoreCase(pageable, name))
                .thenReturn(entityPage);
        when(mapper.toModel(productEntity))
                .thenReturn(domainProduct);
        when(paginationMapper.toDomainResult(entityPage, List.of(domainProduct)))
                .thenReturn(expectedResult);

        // When
        PaginatedResult<Product> result = productRepositoryAdapter.findAllByNameContainingIgnoreCase(paginationQuery, name);

        // Then
        assertEquals(1, result.totalElements());
        assertEquals(domainProduct, result.content()
                .getFirst());

        verify(paginationMapper).toSpringPageable(paginationQuery);
        verify(productJpaRepository).findAllByNameContainingIgnoreCase(pageable, name);
        verify(mapper).toModel(productEntity);
        verify(paginationMapper).toDomainResult(entityPage, List.of(domainProduct));
    }

    @Test
    @DisplayName("Should find product by ID and map to model")
    void findById_ShouldReturnMappedProduct() {
        // Given
        UUID id = UUID.randomUUID();
        ProductEntity productEntity = new ProductEntity();
        Product domainProduct = new Product(
                id,
                "Test product",
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now()
        );

        when(productJpaRepository.findById(id))
                .thenReturn(Optional.of(productEntity));
        when(mapper.toModel(productEntity))
                .thenReturn(domainProduct);

        // When
        Optional<Product> result = productRepositoryAdapter.findById(id.toString());

        // Then
        assertTrue(result.isPresent());
        assertEquals(domainProduct, result.get());
        verify(productJpaRepository).findById(id);
        verify(mapper).toModel(productEntity);
    }

    @Test
    @DisplayName("Should save product and map persisted entity to model")
    void save_ShouldDelegateToJpaRepositoryAndMapToModel() {
        // Given
        Product domainProduct = new Product(
                null,
                "New Product",
                "Description",
                new BigDecimal("99.99"),
                10,
                null
        );

        ProductEntity entityToSave = new ProductEntity();
        ProductEntity savedEntity = new ProductEntity();
        UUID generatedId = UUID.randomUUID();
        Instant createdAt = Instant.now();

        Product persistedProduct = new Product(
                generatedId,
                "New Product",
                "Description",
                new BigDecimal("99.99"),
                10,
                createdAt
        );

        when(productEntityMapper.toEntity(domainProduct)).thenReturn(entityToSave);
        when(productJpaRepository.save(entityToSave)).thenReturn(savedEntity);
        when(mapper.toModel(savedEntity)).thenReturn(persistedProduct);

        // When
        Product result = productRepositoryAdapter.save(domainProduct);

        // Then
        assertEquals(persistedProduct, result);
        verify(productEntityMapper).toEntity(domainProduct);
        verify(productJpaRepository).save(entityToSave);
        verify(mapper).toModel(savedEntity);
    }

    @Test
    @DisplayName("Should return true when product exists by name")
    void existsByName_ShouldReturnTrueWhenProductExists() {
        // Given
        String name = "Existing Product";
        when(productJpaRepository.existsByName(name)).thenReturn(true);

        // When
        boolean result = productRepositoryAdapter.existsByName(name);

        // Then
        assertTrue(result);
        verify(productJpaRepository).existsByName(name);
    }

    @Test
    @DisplayName("Should return false when product does not exist by name")
    void existsByName_ShouldReturnFalseWhenProductDoesNotExist() {
        // Given
        String name = "Non-existent Product";
        when(productJpaRepository.existsByName(name)).thenReturn(false);

        // When
        boolean result = productRepositoryAdapter.existsByName(name);

        // Then
        assertFalse(result);
        verify(productJpaRepository).existsByName(name);
    }

    @Test
    @DisplayName("Should delegate deleteById to JPA repository with converted UUID")
    void deleteById_ShouldDelegateToJpaRepository() {
        // Given
        UUID id = UUID.randomUUID();

        // When
        productRepositoryAdapter.deleteById(id.toString());

        // Then
        verify(productJpaRepository).deleteById(id);
    }
}
