package br.com.devpasso.order_management.application.service;

import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteProductsServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private DeleteProductsService deleteProductsService;

    @Test
    @DisplayName("Should delete product when product exists")
    void execute_WithExistingId_ShouldDeleteProduct() {
        // Given
        UUID id = UUID.randomUUID();
        Product product = new Product(
                id,
                "Test Product",
                "Description",
                new BigDecimal("100.00"),
                10,
                Instant.now()
        );

        when(productRepository.findById(id.toString())).thenReturn(Optional.of(product));

        // When
        deleteProductsService.execute(id.toString());

        // Then
        verify(productRepository).findById(id.toString());
        verify(productRepository).deleteById(id.toString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void execute_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given
        UUID id = UUID.randomUUID();
        when(productRepository.findById(id.toString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deleteProductsService.execute(id.toString()));
        verify(productRepository).findById(id.toString());
        verify(productRepository, never()).deleteById(any());
    }
}
