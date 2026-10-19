package br.com.devpasso.order_management.infrastructure.config.usecase.products;

import br.com.devpasso.order_management.application.mapper.products.CreateProductResultMapper;
import br.com.devpasso.order_management.application.service.products.CreateProductsService;
import br.com.devpasso.order_management.application.usecase.products.CreateProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CreateProductsUseCaseConfig {
    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepositoryPort productRepositoryPort,
                                                     CreateProductResultMapper createProductResultMapper) {
        return new CreateProductsService(productRepositoryPort,
                createProductResultMapper);
    }
}
