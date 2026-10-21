package br.com.devpasso.order_management.infrastructure.config.usecase.orders;

import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.application.service.orders.FindOrdersService;
import br.com.devpasso.order_management.application.usecase.orders.ListOrdersUseCase;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListOrderUseCaseConfig {
    @Bean
    public ListOrdersUseCase listOrdersUseCase(OrderResultMapper orderResultMapper,
                                               OrderRepositoryPort orderRepositoryPort) {
        return new FindOrdersService(orderResultMapper, orderRepositoryPort);
    }
}
