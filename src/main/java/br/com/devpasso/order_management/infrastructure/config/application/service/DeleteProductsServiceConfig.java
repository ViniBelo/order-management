package br.com.devpasso.order_management.infrastructure.config.application.service;

import br.com.devpasso.order_management.application.service.DeleteProductsService;
import br.com.devpasso.order_management.application.usecase.DeleteProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeleteProductsServiceConfig {
    @Bean
    public DeleteProductUseCase deleteProductUseCase(ProductRepository productRepository) {
        return new DeleteProductsService(productRepository);
    }
}
