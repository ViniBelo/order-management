package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.infrastructure.persistence.entity.Product;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private ProductRepositoryAdapter productRepositoryAdapter;

    @Test
    void findAllByNameContainingIgnoreCase_ShouldDelegateToJpaRepository() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        String name = "test";
        Page<Product> expectedPage = new PageImpl<>(List.of(new Product()));
        
        when(productJpaRepository.findAllByNameContainingIgnoreCase(pageable, name))
                .thenReturn(expectedPage);

        // When
        Page<Product> result = productRepositoryAdapter.findAllByNameContainingIgnoreCase(pageable, name);

        // Then
        assertEquals(expectedPage, result);
        verify(productJpaRepository).findAllByNameContainingIgnoreCase(pageable, name);
    }
}
