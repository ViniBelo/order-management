package br.com.devpasso.order_management.infrastructure.config.usecase.orders;

import br.com.devpasso.order_management.application.mapper.orders.OrderResultMapper;
import br.com.devpasso.order_management.application.service.orders.CreateOrdersService;
import br.com.devpasso.order_management.application.usecase.orders.CreateOrderUseCase;
import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateOrderUserCaseConfig {
    @Bean
    public CreateOrderUseCase createOrderUserCase(OrderResultMapper orderResultMapper,
                                                  OrderRepositoryPort orderRepositoryPort) {
        return new CreateOrdersService(orderResultMapper, orderRepositoryPort);
    }
}
