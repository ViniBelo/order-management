package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.application.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.application.dto.response.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
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
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsControllerTest {

    @Mock
    private ListProductsUseCase listProductsUseCase;
    
    @Mock
    private WebPaginationMapper paginationMapper;

    @Mock
    private ProductResponseMapper mapper;

    @InjectMocks
    private ProductsController productsController;

    @Test
    @DisplayName("Should return paginated products")
    void listAll_ShouldReturnOkWithPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Test";

        Product product = new Product(UUID.randomUUID(),
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5, Instant.now());

        PaginatedResult<Product> productPaginatedResult = new PaginatedResult<>(
                List.of(product), 0, 20, 1, 1
        );

        ProductResponse response = new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(productPaginatedResult);
        when(mapper.toResponse(product))
                .thenReturn(response);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = productsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK,
                result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().totalElements());
        assertEquals(response, result.getBody().content().getFirst());

        verify(listProductsUseCase).execute(paginationQuery, name);
        verify(mapper).toResponse(product);
    }
}