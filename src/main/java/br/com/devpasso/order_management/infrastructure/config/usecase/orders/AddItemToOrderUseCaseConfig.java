package br.com.devpasso.order_management.infrastructure.config.usecase.orders;

import br.com.devpasso.order_management.application.mapper.orders.OrderItemResultMapper;
import br.com.devpasso.order_management.application.service.orders.AddItemToOrderService;
import br.com.devpasso.order_management.application.usecase.orders.AddItemToOrderUseCase;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AddItemToOrderUseCaseConfig {
    @Bean
    public AddItemToOrderUseCase addItemToOrderUseCase(OrderItemResultMapper orderItemResultMapper,
                                                       OrderRepositoryPort orderRepositoryPort,
                                                       ProductRepositoryPort productRepositoryPort) {
        return new AddItemToOrderService(orderItemResultMapper,
                orderRepositoryPort,
                productRepositoryPort);
    }
}
