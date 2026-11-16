package br.com.devpasso.order_management.application.service.products;

import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
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
    private ProductRepositoryPort productRepositoryPort;

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

        when(productRepositoryPort.findById(id)).thenReturn(Optional.of(product));

        // When
        deleteProductsService.execute(id);

        // Then
        verify(productRepositoryPort).findById(id);
        verify(productRepositoryPort).deleteById(id);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void execute_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given
        UUID id = UUID.randomUUID();
        when(productRepositoryPort.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> deleteProductsService.execute(id));
        verify(productRepositoryPort).findById(id);
        verify(productRepositoryPort, never()).deleteById(any());
    }
}
