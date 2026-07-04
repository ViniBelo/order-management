package br.com.devpasso.order_management.api.controller;

import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import java.util.UUID;

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

    private List<ProductEntity> savedProducts;

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

        savedProducts = productJpaRepository.saveAll(List.of(p1, p2));
    }

    @Test
    @DisplayName("Should list all products")
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
    @DisplayName("Should find product by ID")
    void shouldFindProductById() throws Exception {
        UUID id = savedProducts.getFirst().getId();
        mockMvc.perform(get("/v1/products/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Apple iPhone"))
                .andExpect(jsonPath("$.price").value(999.99));
    }

    @Test
    @DisplayName("Should return 404 Not Found when product does not exist")
    void shouldReturn404WhenNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(get("/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("The requested resource could not be found."));
    }

    @Test
    @DisplayName("Should filter products by name")
    void shouldFilterProductsByName() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("name", "samsung"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Samsung Galaxy"));
    }

    @Test
    @DisplayName("Should filter products by name ignoring case")
    void shouldFilterProductsByNameCaseInsensitive() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("name", "IPHONE"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Apple iPhone"));
    }

    @Test
    @DisplayName("Should return empty list when no products match the search term")
    void shouldReturnEmptyListWhenNoProductsFound() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("name", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("Should return first page when page is 0 and size is 1")
    void shouldReturnFirstPage() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("page", "0")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Apple iPhone"));
    }

    @Test
    @DisplayName("Should return last page when size is greater than total elements")
    void shouldReturnLastPage() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Samsung Galaxy"));
    }

    @Test
    @DisplayName("Should return empty list for invalid page number")
    void shouldReturnEmptyListForInvalidPage() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("page", "2")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when sort parameter is invalid")
    void shouldHandleInvalidSortParameter() throws Exception {
        mockMvc.perform(get("/v1/products")
                        .param("sort", "invalidField,asc"))
                .andExpect(status().isBadRequest());
    }
}
