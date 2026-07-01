package br.com.devpasso.order_management.infrastructure.config.domain;

import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.application.service.ProductsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsServiceConfig {
    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepository productRepository,
                                                   WebPaginationMapper paginationMapper) {
        return new ProductsService(productRepository, paginationMapper);
    }
}
