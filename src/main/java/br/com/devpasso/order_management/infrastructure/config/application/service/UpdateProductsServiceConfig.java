package br.com.devpasso.order_management.infrastructure.config.application.service;

import br.com.devpasso.order_management.application.service.UpdateProductsService;
import br.com.devpasso.order_management.application.usecase.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateProductsServiceConfig {
    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository productRepository) {
        return new UpdateProductsService(productRepository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository productRepository) {
        return new UpdateProductsService(productRepository);
    }
}
