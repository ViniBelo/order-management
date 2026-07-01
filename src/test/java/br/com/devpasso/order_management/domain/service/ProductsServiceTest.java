package br.com.devpasso.order_management.domain.service;

import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.service.ProductsService;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductsService productsService;

    @Test
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
}
