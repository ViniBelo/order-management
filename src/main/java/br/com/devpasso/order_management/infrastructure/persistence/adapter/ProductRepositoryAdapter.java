package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.infrastructure.persistence.entity.Product;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public class ProductRepositoryAdapter implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Page<Product> listAll(Pageable pageable) {
        return productJpaRepository.findAll(pageable);
    }
}
