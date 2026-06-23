package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
class ProductsControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @BeforeEach
    void setUp() {
        productJpaRepository.deleteAll();
        
        ProductEntity p1 = new ProductEntity();
        p1.changeName("Apple iPhone");
        p1.changePrice(new BigDecimal("999.99"));
        p1.changeStockQuantity(10);

        ProductEntity p2 = new ProductEntity();
        p2.changeName("Samsung Galaxy");
        p2.changePrice(new BigDecimal("899.99"));
        p2.changeStockQuantity(15);

        productJpaRepository.saveAll(List.of(p1, p2));
    }

    @Test
    void shouldListAllProducts() {
        webTestClient.get().uri("/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content.length()").isEqualTo(2)
                .jsonPath("$.content[0].name").isEqualTo("Apple iPhone")
                .jsonPath("$.content[1].name").isEqualTo("Samsung Galaxy");
    }

    @Test
    void shouldFilterProductsByName() {
        webTestClient.get().uri(uriBuilder -> uriBuilder
                        .path("/v1/products")
                        .queryParam("name", "samsung")
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.content.length()").isEqualTo(1)
                .jsonPath("$.content[0].name").isEqualTo("Samsung Galaxy");
    }
}
