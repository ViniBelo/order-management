package br.com.devpasso.order_management.api.controller.products;

import br.com.devpasso.order_management.application.usecase.DeleteProductUseCase;
import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteProductsControllerTest {
    @Mock
    private DeleteProductUseCase deleteProductUseCase;

    @InjectMocks
    private DeleteProductsController deleteProductsController;

    @Test
    @DisplayName("Should delete product and return no content status")
    void deleteProduct_ShouldReturnNoContent() {
        // Given
        UUID productId = UUID.randomUUID();

        // When
        ResponseEntity<Void> result = deleteProductsController.deleteProduct(productId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deleteProductUseCase).execute(productId.toString());
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent product")
    void deleteProduct_ShouldThrowResourceNotFoundExceptionWhenProductNotFound() {
        // Given
        UUID productId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Product not found for ID: " + productId))
                .when(deleteProductUseCase).execute(productId.toString());

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteProductsController.deleteProduct(productId)
        );

        assertEquals("Product not found for ID: " + productId, exception.getMessage());
        verify(deleteProductUseCase).execute(productId.toString());
    }
}
