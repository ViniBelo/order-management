package br.com.devpasso.order_management.infrastructure.config.persistence;

import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.ProductRepositoryAdapter;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductRepositoryAdapterConfig {

    @Bean
    public InfraPaginationMapper infraPaginationMapper() {
        return new InfraPaginationMapper();
    }

    @Bean
    public ProductModelMapper productModelMapper() {
        return new ProductModelMapper();
    }

    @Bean
    public ProductEntityMapper productEntityMapper() {
        return new ProductEntityMapper();
    }

    @Bean
    public ProductRepository productRepository(ProductJpaRepository productJpaRepository,
                                               InfraPaginationMapper infraPaginationMapper,
                                               ProductEntityMapper productEntityMapper,
                                               ProductModelMapper productModelMapper) {
        return new ProductRepositoryAdapter(productJpaRepository,
                infraPaginationMapper,
                productModelMapper,
                productEntityMapper);
    }
}
