package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderItemUseCase;
import br.com.devpasso.order_management.domain.exception.InvalidOrderStateException;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DeleteOrdersControllerTest {

    @Mock
    private DeleteOrderItemUseCase deleteOrderItemUseCase;

    @InjectMocks
    private DeleteOrdersController deleteOrdersController;

    @Test
    @DisplayName("Should delete order item and return 204 No Content status")
    void deleteOrder_ShouldReturnNoContent() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        doNothing().when(deleteOrderItemUseCase).execute(orderId, itemId);

        // When
        ResponseEntity<Void> result = deleteOrdersController.deleteOrder(orderId, itemId);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deleteOrderItemUseCase).execute(orderId, itemId);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order is not found")
    void deleteOrder_ShouldThrowResourceNotFoundExceptionWhenOrderNotFound() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Order item not found for ID: " + orderId))
                .when(deleteOrderItemUseCase).execute(orderId, itemId);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteOrdersController.deleteOrder(orderId, itemId)
        );

        assertEquals("Order item not found for ID: " + orderId, exception.getMessage());
        verify(deleteOrderItemUseCase).execute(orderId, itemId);
    }

    @Test
    @DisplayName("Should throw InvalidOrderStateException when order is not in draft status")
    void deleteOrder_ShouldThrowInvalidOrderStateExceptionWhenOrderNotInDraft() {
        // Given
        UUID orderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();
        doThrow(new InvalidOrderStateException("Order is not in draft status"))
                .when(deleteOrderItemUseCase).execute(orderId, itemId);

        // When & Then
        InvalidOrderStateException exception = assertThrows(
                InvalidOrderStateException.class,
                () -> deleteOrdersController.deleteOrder(orderId, itemId)
        );

        assertEquals("Order is not in draft status", exception.getMessage());
        verify(deleteOrderItemUseCase).execute(orderId, itemId);
    }
}
