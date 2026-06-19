package br.com.devpasso.order_management.domain.service;

import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.service.mapper.ProductModelMapper;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductModelMapper mapper;

    @InjectMocks
    private ProductsService productsService;

    @Test
    void execute_ShouldReturnPaginatedProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String name = "Test";
        
        br.com.devpasso.order_management.infrastructure.persistence.entity.Product entity = 
            new br.com.devpasso.order_management.infrastructure.persistence.entity.Product();
        entity.changeName("Test Product");
        
        Page<br.com.devpasso.order_management.infrastructure.persistence.entity.Product> entityPage = 
            new PageImpl<>(List.of(entity), pageable, 1);
        
        Product domainProduct = new Product(entity.getId(),
                entity.getName(),
                "Test description",
                new BigDecimal("10.0"),
                5,
                Instant.now());
        
        when(productRepository.findAllByNameContainingIgnoreCase(pageable, name)).thenReturn(entityPage);
        when(mapper.toModel(entity)).thenReturn(domainProduct);

        // When
        Page<Product> result = productsService.execute(pageable, name);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(domainProduct, result.getContent()
                .getFirst());
        verify(productRepository).findAllByNameContainingIgnoreCase(pageable, name);
        verify(mapper).toModel(entity);
    }
}
