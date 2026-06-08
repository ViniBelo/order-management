package br.com.devpasso.order_management.domain.service.mapper;

import br.com.devpasso.order_management.domain.model.Product;
import org.springframework.stereotype.Service;

@Service
public class ProductModelMapper {
    public Product toModel (br.com.devpasso.order_management.infrastructure.persistence.entity.Product product) {
        return new Product(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getCreatedAt()
        );
    }
}
