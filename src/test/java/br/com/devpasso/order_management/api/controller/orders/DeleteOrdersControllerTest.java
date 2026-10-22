package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderUseCase;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteOrdersControllerTest {

    @Mock
    private DeleteOrderUseCase deleteOrderUseCase;

    @InjectMocks
    private DeleteOrdersController deleteOrdersController;

    @Test
    @DisplayName("Should delete order and return no content status")
    void deleteOrder_ShouldReturnNoContent() {
        // Given
        UUID orderId = UUID.randomUUID();

        // When
        ResponseEntity<Void> result = deleteOrdersController.deleteOrder(orderId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deleteOrderUseCase).execute(orderId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when deleting non-existent order")
    void deleteOrder_ShouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
        // Given
        UUID orderId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Order not found for ID: " + orderId))
                .when(deleteOrderUseCase).execute(orderId);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteOrdersController.deleteOrder(orderId)
        );

        assertEquals("Order not found for ID: " + orderId, exception.getMessage());
        verify(deleteOrderUseCase).execute(orderId);
    }
}
