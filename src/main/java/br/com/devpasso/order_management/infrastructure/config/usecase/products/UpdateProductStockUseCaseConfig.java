package br.com.devpasso.order_management.infrastructure.config.usecase.products;

import br.com.devpasso.order_management.application.mapper.products.ProductResultMapper;
import br.com.devpasso.order_management.application.service.products.UpdateProductsService;
import br.com.devpasso.order_management.application.usecase.products.UpdateProductStockUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UpdateProductStockUseCaseConfig {
    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepositoryPort productRepositoryPort,
                                                               ProductResultMapper productResultMapper) {
        return new UpdateProductsService(productRepositoryPort,
                productResultMapper);
    }
}
