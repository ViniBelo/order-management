package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @InjectMocks
    private ProductRepositoryAdapter productRepositoryAdapter;

    @Test
    @DisplayName("Should delegate to JPA repository")
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
}
