package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.application.dto.ProductResponse;
import br.com.devpasso.order_management.application.mapper.ProductResponseMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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
    private ProductResponseMapper mapper;

    @InjectMocks
    private ProductsController productsController;

    @Test
    void listAll_ShouldReturnOkWithPaginatedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 20);
        String name = "Test";
        
        Product product = new Product(UUID.randomUUID(),
                "Test Product",
                "Desc", new BigDecimal("10.0"),
                5, Instant.now());
        Page<Product> productPage = new PageImpl<>(List.of(product),
                pageable,
                1);
        
        ProductResponse response = new ProductResponse(product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity());
        
        when(listProductsUseCase.execute(pageable, name))
                .thenReturn(productPage);
        when(mapper.toResponse(product))
                .thenReturn(response);

        // When
        ResponseEntity<?> result = productsController.listAll(pageable, name);

        // Then
        assertEquals(HttpStatus.OK,
                result.getStatusCode());
        assertNotNull(result.getBody());
        verify(listProductsUseCase).execute(pageable, name);
        verify(mapper).toResponse(product);
    }
}