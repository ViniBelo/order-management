package br.com.devpasso.order_management.infrastructure.config.usecase.products;

import br.com.devpasso.order_management.application.mapper.products.ProductResultMapper;
import br.com.devpasso.order_management.application.service.products.UpdateProductsService;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateProductUseCaseConfig {
    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepositoryPort productRepositoryPort,
                                                     ProductResultMapper productResultMapper) {
        return new UpdateProductsService(productRepositoryPort,
                productResultMapper);
    }
}
