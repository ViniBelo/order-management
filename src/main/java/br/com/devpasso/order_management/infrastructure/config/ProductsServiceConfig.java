package br.com.devpasso.order_management.infrastructure.config;

import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.domain.service.ProductsService;
import br.com.devpasso.order_management.domain.service.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.ProductRepositoryAdapter;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsServiceConfig {

    @Bean
    public ProductRepository productRepository(ProductJpaRepository productJpaRepository) {
        return new ProductRepositoryAdapter(productJpaRepository);
    }

    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepository productRepository,
                                                   ProductModelMapper mapper) {
        return new ProductsService(productRepository, mapper);
    }
}
