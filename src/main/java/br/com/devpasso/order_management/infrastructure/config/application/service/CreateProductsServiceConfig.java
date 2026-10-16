package br.com.devpasso.order_management.infrastructure.config.application.service;

import br.com.devpasso.order_management.application.mapper.products.CreateProductResultMapper;
import br.com.devpasso.order_management.application.service.products.CreateProductsService;
import br.com.devpasso.order_management.application.usecase.products.CreateProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateProductsServiceConfig {
    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository,
                                                     CreateProductResultMapper createProductResultMapper) {
        return new CreateProductsService(productRepository,
                createProductResultMapper);
    }
}
