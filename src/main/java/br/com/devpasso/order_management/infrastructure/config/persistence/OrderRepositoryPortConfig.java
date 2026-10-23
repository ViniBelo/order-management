package br.com.devpasso.order_management.infrastructure.config.persistence;

import br.com.devpasso.order_management.domain.repository.OrderRepositoryPort;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.OrderRepositoryAdapter;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.OrderEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.repository.OrderJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderRepositoryPortConfig {
    @Bean
    public OrderRepositoryPort orderRepository(OrderJpaRepository orderJpaRepository,
                                                InfraPaginationMapper paginationMapper,
                                                OrderEntityMapper orderEntityMapper) {
        return new OrderRepositoryAdapter(orderJpaRepository,
                paginationMapper,
                orderEntityMapper);
    }
}
