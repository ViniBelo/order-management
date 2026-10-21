package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.api.dto.response.PaginatedResponse;
import br.com.devpasso.order_management.api.dto.response.products.ProductResponse;
import br.com.devpasso.order_management.api.mapper.products.response.PaginatedResponseMapper;
import br.com.devpasso.order_management.api.mapper.products.response.ProductResponseMapper;
import br.com.devpasso.order_management.application.dto.result.PaginatedResult;
import br.com.devpasso.order_management.application.dto.result.products.ProductResult;
import br.com.devpasso.order_management.application.mapper.PaginationQueryMapper;
import br.com.devpasso.order_management.application.usecase.products.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.products.ListProductsUseCase;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ViewProductsControllerTest {
    @Mock
    private ListProductsUseCase listProductsUseCase;

    @Mock
    private FindProductByIdUseCase findProductByIdUseCase;

    @Mock
    private PaginationQueryMapper paginationMapper;

    @Mock
    private PaginatedResponseMapper paginatedResponseMapper;

    @Mock
    private ProductResponseMapper productResponseMapper;

    @InjectMocks
    private ViewProductsController viewProductsController;

    @Test
    @DisplayName("Should return paginated products")
    void listAll_ShouldReturnOkWithPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Test";

        ProductResult product = new ProductResult(UUID.randomUUID(),
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5);

        PaginatedResult<ProductResult> productPaginatedResult = new PaginatedResult<>(
                List.of(product), 0, 20, 1, 1
        );

        ProductResponse productResponse = new ProductResponse(product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity());

        List<ProductResponse> productResponses = List.of(productResponse);
        PaginatedResponse<ProductResponse> paginatedResponse = new PaginatedResponse<>(
                productResponses,
                0,
                20,
                1,
                1
        );

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(productPaginatedResult);
        when(productResponseMapper.toResponse(product))
                .thenReturn(productResponse);
        when(paginatedResponseMapper.from(productPaginatedResult, productResponses))
                .thenReturn(paginatedResponse);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = viewProductsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK,
                result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().totalElements());
        assertEquals(productResponse, result.getBody().content().getFirst());

        verify(listProductsUseCase).execute(paginationQuery, name);
        verify(productResponseMapper).toResponse(product);
    }

    @Test
    @DisplayName("Should return empty paginated response when no products found")
    void listAll_ShouldReturnOkWithEmptyPaginatedProducts() {
        // Given
        PaginationQuery paginationQuery = new PaginationQuery(0, 20, "");
        Pageable pageable = PageRequest.of(paginationQuery.page(), paginationQuery.size());
        String name = "Nonexistent";

        List<ProductResult> emptyProductResultsList = List.of();
        PaginatedResult<ProductResult> emptyPaginatedResult = new PaginatedResult<>(
                emptyProductResultsList, 0, 20, 0, 0
        );

        List<ProductResponse> emptyProductResponsesList = List.of();
        PaginatedResponse<ProductResponse> emptyPaginatedResponse = new PaginatedResponse<>(
                emptyProductResponsesList, 0, 20, 0, 0
        );

        when(paginationMapper.toDomainQuery(pageable))
                .thenReturn(paginationQuery);
        when(listProductsUseCase.execute(paginationQuery, name))
                .thenReturn(emptyPaginatedResult);
        when(paginatedResponseMapper.from(emptyPaginatedResult, emptyProductResponsesList))
                .thenReturn(emptyPaginatedResponse);

        // When
        ResponseEntity<PaginatedResponse<ProductResponse>> result = viewProductsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(0, result.getBody().totalElements());
        assertTrue(result.getBody().content().isEmpty());

        verify(paginationMapper).toDomainQuery(pageable);
        verify(listProductsUseCase).execute(paginationQuery, name);
        verifyNoInteractions(productResponseMapper);
    }

    @Test
    @DisplayName("Should return product by ID")
    void findById_ShouldReturnOkWithProduct() {
        // Given
        UUID id = UUID.randomUUID();
        ProductResult product = new ProductResult(id,
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5);

        ProductResponse response = new ProductResponse(product.id(),
                product.name(),
                product.description(),
                product.price(),
                product.stockQuantity());

        when(findProductByIdUseCase.execute(id.toString()))
                .thenReturn(product);
        when(productResponseMapper.toResponse(product))
                .thenReturn(response);

        // When
        ResponseEntity<ProductResponse> result = viewProductsController.findById(id);

        // Then
        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(response, result.getBody());

        verify(findProductByIdUseCase).execute(id.toString());
        verify(productResponseMapper).toResponse(product);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found by ID")
    void findById_ShouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        UUID id = UUID.randomUUID();

        when(findProductByIdUseCase.execute(id.toString()))
                .thenThrow(new ResourceNotFoundException("Product not found for ID: " + id));

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> viewProductsController.findById(id)
        );

        assertEquals("Product not found for ID: " + id, exception.getMessage());
        verify(findProductByIdUseCase).execute(id.toString());
        verifyNoInteractions(productResponseMapper);
    }
}
