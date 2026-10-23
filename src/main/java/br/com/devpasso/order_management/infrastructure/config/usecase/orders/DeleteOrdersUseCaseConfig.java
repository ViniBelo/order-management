package br.com.devpasso.order_management.infrastructure.config.usecase.orders;

import br.com.devpasso.order_management.application.service.orders.DeleteOrderItemsServiceItem;
import br.com.devpasso.order_management.application.usecase.orders.DeleteOrderItemUseCase;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeleteOrdersUseCaseConfig {
    @Bean
    public DeleteOrderItemUseCase deleteOrdersUseCase(OrderRepositoryPort orderRepositoryPort) {
        return new DeleteOrderItemsServiceItem(orderRepositoryPort);
    }
}
