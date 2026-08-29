package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductEntityMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ProductRepositoryAdapter implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final InfraPaginationMapper paginationMapper;
    private final ProductModelMapper productModelMapper;
    private final ProductEntityMapper productEntityMapper;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository,
                                    InfraPaginationMapper paginationMapper,
                                    ProductModelMapper productModelMapper,
                                    ProductEntityMapper productEntityMapper) {
        this.productJpaRepository = productJpaRepository;
        this.paginationMapper = paginationMapper;
        this.productModelMapper = productModelMapper;
        this.productEntityMapper = productEntityMapper;
    }

    @Override
    public PaginatedResult<Product> findAllByNameContainingIgnoreCase(PaginationQuery paginationQuery, String name) {
        Page<ProductEntity> productEntities =
                productJpaRepository.findAllByNameContainingIgnoreCase(
                        paginationMapper.toSpringPageable(paginationQuery),
                        name
                );

        List<Product> products = productEntities.getContent()
                .stream()
                .map(productModelMapper::toModel)
                .toList();

        return paginationMapper.toDomainResult(productEntities, products);
    }

    @Override
    public Optional<Product> findById(String id) {
        return productJpaRepository.findById(UUID.fromString(id))
                .map(productModelMapper::toModel);
    }

    @Override
    public Product save(Product product) {
        ProductEntity persistedProduct =
                productJpaRepository.save(productEntityMapper.toEntity(product));
        return productModelMapper.toModel(persistedProduct);
    }

    @Override
    public boolean existsByName(String name) {
        return  productJpaRepository.existsByName(name);
    }

    @Override
    public void deleteById(String id) {
        productJpaRepository.deleteById(UUID.fromString(id));
    }
}
