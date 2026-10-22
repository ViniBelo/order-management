package br.com.devpasso.order_management.application.service.orders;

import br.com.devpasso.order_management.domain.exception.ResourceNotFoundException;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteOrdersServiceTest {

    @Mock
    private OrderRepositoryPort orderRepositoryPort;

    @InjectMocks
    private DeleteOrdersService deleteOrdersService;

    @Test
    @DisplayName("Should delete order when order exists")
    void execute_WithExistingId_ShouldDeleteOrder() {
        // Given
        UUID id = UUID.randomUUID();
        when(orderRepositoryPort.existsById(id)).thenReturn(true);

        // When
        deleteOrdersService.execute(id);

        // Then
        verify(orderRepositoryPort).existsById(id);
        verify(orderRepositoryPort).deleteById(id);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when order not found")
    void execute_WithNonExistingId_ShouldThrowResourceNotFoundException() {
        // Given
        UUID id = UUID.randomUUID();
        when(orderRepositoryPort.existsById(id)).thenReturn(false);

        // When & Then
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> deleteOrdersService.execute(id)
        );

        assertEquals("Order not found for ID: " + id, exception.getMessage());
        verify(orderRepositoryPort).existsById(id);
        verify(orderRepositoryPort, never()).deleteById(any());
    }
}
