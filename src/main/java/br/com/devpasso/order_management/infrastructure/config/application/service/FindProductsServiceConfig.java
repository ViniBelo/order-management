package br.com.devpasso.order_management.infrastructure.config.application.service;

import br.com.devpasso.order_management.application.service.FindProductsService;
import br.com.devpasso.order_management.application.usecase.*;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FindProductsServiceConfig {
    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepository productRepository) {
        return new FindProductsService(productRepository);
    }

    @Bean
    public FindProductByIdUseCase findProductByIdUseCase(ProductRepository productRepository) {
        return new FindProductsService(productRepository);
    }
}
