package br.com.devpasso.order_management.infrastructure.persistence.adapter;

import br.com.devpasso.order_management.domain.common.PaginatedResult;
import br.com.devpasso.order_management.domain.common.PaginationQuery;
import br.com.devpasso.order_management.domain.model.Product;
import br.com.devpasso.order_management.domain.repository.ProductRepository;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.InfraPaginationMapper;
import br.com.devpasso.order_management.infrastructure.persistence.adapter.mapper.ProductModelMapper;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.springframework.data.domain.Page;

import java.util.List;

public class ProductRepositoryAdapter implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;
    private final InfraPaginationMapper paginationMapper;
    private final ProductModelMapper mapper;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository, InfraPaginationMapper paginationMapper,
                                    ProductModelMapper mapper) {
        this.productJpaRepository = productJpaRepository;
        this.paginationMapper = paginationMapper;
        this.mapper = mapper;
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
                .map(mapper::toModel)
                .toList();

        return paginationMapper.toDomainResult(productEntities, products);
    }
}
