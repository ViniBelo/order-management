package br.com.devpasso.order_management.infrastructure.config.application.service;

import br.com.devpasso.order_management.application.service.CreateProductsService;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateProductsServiceConfig {
    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository) {
        return new CreateProductsService(productRepository);
    }
}
