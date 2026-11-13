package br.com.devpasso.order_management.infrastructure.config.persistence;

import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.ProductRepositoryAdapter;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRepositoryPortConfig {
    @Bean
    public ProductRepositoryPort productRepository(ProductJpaRepository productJpaRepository,
                                                   InfraPaginationMapper infraPaginationMapper,
                                                   ProductModelMapper productModelMapper,
                                                   ProductEntityMapper productEntityMapper) {
        return new ProductRepositoryAdapter(productJpaRepository,
                infraPaginationMapper,
                productModelMapper,
                productEntityMapper);
    }
}
