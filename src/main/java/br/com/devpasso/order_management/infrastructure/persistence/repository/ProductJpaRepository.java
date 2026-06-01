package br.com.devpasso.order_management.infrastructure.persistence.repository;

import br.com.devpasso.order_management.infrastructure.persistence.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProductJpaRepository extends JpaRepository<Product, UUID> { }
