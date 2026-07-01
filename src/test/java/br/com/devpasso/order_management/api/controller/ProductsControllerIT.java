package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class ProductsControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

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
    void shouldListAllProducts() throws Exception {
        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Apple iPhone"))
                .andExpect(jsonPath("$.content[1].name").value("Samsung Galaxy"));
    }

    @Test
    void shouldFilterProductsByName() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("name", "samsung"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Samsung Galaxy"));
    }
}
