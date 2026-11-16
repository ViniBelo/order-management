package br.com.devpasso.order_management.infrastructure.config.usecase.products;

import br.com.devpasso.order_management.application.mapper.products.ProductResultMapper;
import br.com.devpasso.order_management.application.service.products.FindProductsService;
import br.com.devpasso.order_management.application.usecase.products.ListProductsUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ListProductsUseCaseConfig {
    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepositoryPort productRepositoryPort,
                                                   ProductResultMapper productResultMapper) {
        return new FindProductsService(productRepositoryPort,
                productResultMapper);
    }
}
