package br.com.devpasso.order_management.infrastructure.config.domain;

import br.com.devpasso.order_management.application.mapper.WebPaginationMapper;
import br.com.devpasso.order_management.application.usecase.CreateProductUseCase;
import br.com.devpasso.order_management.application.usecase.FindProductByIdUseCase;
import br.com.devpasso.order_management.application.usecase.ListProductsUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductStockUseCase;
import br.com.devpasso.order_management.application.usecase.UpdateProductUseCase;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.application.service.ProductsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProductsServiceConfig {
    @Bean
    public ListProductsUseCase listProductsUseCase(ProductRepository productRepository) {
        return new ProductsService(productRepository);
    }

    @Bean
    public FindProductByIdUseCase findProductByIdUseCase(ProductRepository productRepository) {
        return new ProductsService(productRepository);
    }

    @Bean
    public CreateProductUseCase createProductUseCase(ProductRepository productRepository) {
        return new ProductsService(productRepository);
    }

    @Bean
    public UpdateProductUseCase updateProductUseCase(ProductRepository productRepository) {
        return new ProductsService(productRepository);
    }

    @Bean
    public UpdateProductStockUseCase updateProductStockUseCase(ProductRepository productRepository) {
        return new ProductsService(productRepository);
    }
}
