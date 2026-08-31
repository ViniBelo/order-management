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

import static org.hamcrest.Matchers.matchesPattern;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Test
    @DisplayName("Should create product successfully")
    void shouldCreateProductSuccessfully() throws Exception {
        String requestJson = """
                {
                    "name": "iPad Pro",
                    "description": "Apple tablet",
                    "price": 1199.99,
                    "stockQuantity": 20
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", matchesPattern("^/v1/products/[a-f0-9\\-]+$")))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("iPad Pro"))
                .andExpect(jsonPath("$.description").value("Apple tablet"))
                .andExpect(jsonPath("$.price").value(1199.99))
                .andExpect(jsonPath("$.stockQuantity").value(20))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertTrue(productJpaRepository.existsByName("iPad Pro"));
    }

    @Test
    @DisplayName("Should return 409 Conflict when product name already exists")
    void shouldReturn409ConflictWhenProductNameAlreadyExists() throws Exception {
        String requestJson = """
                {
                    "name": "Apple iPhone",
                    "description": "Another Apple iPhone",
                    "price": 999.99,
                    "stockQuantity": 5
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("Product already exists with name: Apple iPhone"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when name is blank")
    void shouldReturn400BadRequestWhenNameIsBlank() throws Exception {
        String requestJson = """
                {
                    "name": "",
                    "description": "Description",
                    "price": 49.99,
                    "stockQuantity": 10
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when name is shorter than 3 characters")
    void shouldReturn400BadRequestWhenNameIsTooShort() throws Exception {
        String requestJson = """
                {
                    "name": "ab",
                    "description": "Description",
                    "price": 49.99,
                    "stockQuantity": 10
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when price is null")
    void shouldReturn400BadRequestWhenPriceIsNull() throws Exception {
        String requestJson = """
                {
                    "name": "Valid Product Name",
                    "description": "Description",
                    "price": null,
                    "stockQuantity": 10
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when price is less than 0.01")
    void shouldReturn400BadRequestWhenPriceIsLessThanMinimum() throws Exception {
        String requestJson = """
                {
                    "name": "Valid Product Name",
                    "description": "Description",
                    "price": 0.00,
                    "stockQuantity": 10
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when stock quantity is null")
    void shouldReturn400BadRequestWhenStockQuantityIsNull() throws Exception {
        String requestJson = """
                {
                    "name": "Valid Product Name",
                    "description": "Description",
                    "price": 49.99,
                    "stockQuantity": null
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when stock quantity is negative")
    void shouldReturn400BadRequestWhenStockQuantityIsNegative() throws Exception {
        String requestJson = """
                {
                    "name": "Valid Product Name",
                    "description": "Description",
                    "price": 49.99,
                    "stockQuantity": -1
                }
                """;

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when request body is empty")
    void shouldReturn400BadRequestWhenRequestBodyIsEmpty() throws Exception {
        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should update product successfully and retain id, createdAt, and stockQuantity")
    void shouldUpdateProductSuccessfully() throws Exception {
        ProductEntity existing = savedProducts.getFirst();
        UUID id = existing.getId();

        String requestJson = """
                {
                    "name": "Apple iPhone 15 Pro",
                    "description": "Updated Description",
                    "price": 1099.99
                }
                """;

        mockMvc.perform(put("/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value("Apple iPhone 15 Pro"))
                .andExpect(jsonPath("$.description").value("Updated Description"))
                .andExpect(jsonPath("$.price").value(1099.99))
                .andExpect(jsonPath("$.stockQuantity").value(existing.getStockQuantity()));

        ProductEntity updatedInDb = productJpaRepository.findById(id).orElseThrow();
        assertEquals(id, updatedInDb.getId());
        assertEquals("Apple iPhone 15 Pro", updatedInDb.getName());
        assertEquals("Updated Description", updatedInDb.getDescription());
        assertEquals(new BigDecimal("1099.99"), updatedInDb.getPrice());
        assertEquals(existing.getStockQuantity(), updatedInDb.getStockQuantity());
        assertEquals(existing.getCreatedAt(), updatedInDb.getCreatedAt());
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating non-existent product")
    void shouldReturn404WhenUpdatingNonExistentProduct() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String requestJson = """
                {
                    "name": "Non-existent Product",
                    "description": "Description",
                    "price": 49.99
                }
                """;

        mockMvc.perform(put("/v1/products/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("The requested resource could not be found."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when update name is blank")
    void shouldReturn400BadRequestWhenUpdateNameIsBlank() throws Exception {
        UUID id = savedProducts.getFirst().getId();
        String requestJson = """
                {
                    "name": "",
                    "description": "Description",
                    "price": 49.99
                }
                """;

        mockMvc.perform(put("/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when update price is less than 0.01")
    void shouldReturn400BadRequestWhenUpdatePriceIsLessThanMinimum() throws Exception {
        UUID id = savedProducts.getFirst().getId();
        String requestJson = """
                {
                    "name": "Valid Product Name",
                    "description": "Description",
                    "price": 0.00
                }
                """;

        mockMvc.perform(put("/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 409 Conflict when updating product name to an existing name")
    void shouldReturn409ConflictWhenUpdatingProductNameToExistingName() throws Exception {
        UUID id = savedProducts.get(0).getId();
        String existingOtherName = savedProducts.get(1).getName();

        String requestJson = """
                {
                    "name": "%s",
                    "description": "Updated Description",
                    "price": 1099.99
                }
                """.formatted(existingOtherName);

        mockMvc.perform(put("/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Conflict"))
                .andExpect(jsonPath("$.detail").value("Product already exists with name: " + existingOtherName));
    }

    @Test
    @DisplayName("Should update product successfully when name is unchanged")
    void shouldUpdateProductSuccessfullyWhenNameIsUnchanged() throws Exception {
        ProductEntity existing = savedProducts.getFirst();
        UUID id = existing.getId();
        String currentName = existing.getName();

        String requestJson = """
                {
                    "name": "%s",
                    "description": "New Description Same Name",
                    "price": 1299.99
                }
                """.formatted(currentName);

        mockMvc.perform(put("/v1/products/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(currentName))
                .andExpect(jsonPath("$.description").value("New Description Same Name"))
                .andExpect(jsonPath("$.price").value(1299.99))
                .andExpect(jsonPath("$.stockQuantity").value(existing.getStockQuantity()));

        ProductEntity updatedInDb = productJpaRepository.findById(id).orElseThrow();
        assertEquals(id, updatedInDb.getId());
        assertEquals(currentName, updatedInDb.getName());
        assertEquals("New Description Same Name", updatedInDb.getDescription());
        assertEquals(new BigDecimal("1299.99"), updatedInDb.getPrice());
        assertEquals(existing.getStockQuantity(), updatedInDb.getStockQuantity());
        assertEquals(existing.getCreatedAt(), updatedInDb.getCreatedAt());
    }

    @Test
    @DisplayName("Should update product stock successfully")
    void shouldUpdateProductStockSuccessfully() throws Exception {
        ProductEntity existing = savedProducts.getFirst();
        UUID id = existing.getId();

        String requestJson = """
                {
                    "stockQuantity": 50
                }
                """;

        mockMvc.perform(patch("/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.name").value(existing.getName()))
                .andExpect(jsonPath("$.description").value(existing.getDescription()))
                .andExpect(jsonPath("$.price").value(existing.getPrice().doubleValue()))
                .andExpect(jsonPath("$.stockQuantity").value(50));

        ProductEntity updatedInDb = productJpaRepository.findById(id).orElseThrow();
        assertEquals(id, updatedInDb.getId());
        assertEquals(existing.getName(), updatedInDb.getName());
        assertEquals(existing.getDescription(), updatedInDb.getDescription());
        assertEquals(existing.getPrice(), updatedInDb.getPrice());
        assertEquals(50, updatedInDb.getStockQuantity());
        assertEquals(existing.getCreatedAt(), updatedInDb.getCreatedAt());
    }

    @Test
    @DisplayName("Should update product stock to zero successfully")
    void shouldUpdateProductStockToZeroSuccessfully() throws Exception {
        ProductEntity existing = savedProducts.getFirst();
        UUID id = existing.getId();

        String requestJson = """
                {
                    "stockQuantity": 0
                }
                """;

        mockMvc.perform(patch("/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.stockQuantity").value(0));

        ProductEntity updatedInDb = productJpaRepository.findById(id).orElseThrow();
        assertEquals(0, updatedInDb.getStockQuantity());
    }

    @Test
    @DisplayName("Should return 404 Not Found when updating stock of non-existent product")
    void shouldReturn404WhenUpdatingStockOfNonExistentProduct() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        String requestJson = """
                {
                    "stockQuantity": 50
                }
                """;

        mockMvc.perform(patch("/v1/products/{id}/stock", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("The requested resource could not be found."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when stock quantity is negative")
    void shouldReturn400WhenStockIsNegative() throws Exception {
        UUID id = savedProducts.getFirst().getId();
        String requestJson = """
                {
                    "stockQuantity": -5
                }
                """;

        mockMvc.perform(patch("/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when stock quantity is null or body is empty")
    void shouldReturn400WhenStockIsNull() throws Exception {
        UUID id = savedProducts.getFirst().getId();

        mockMvc.perform(patch("/v1/products/{id}/stock", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when ID is not a valid UUID")
    void shouldReturn400WhenUpdatingStockWithInvalidUUID() throws Exception {
        String requestJson = """
                {
                    "stockQuantity": 10
                }
                """;

        mockMvc.perform(patch("/v1/products/{id}/stock", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should delete product successfully (soft-delete)")
    void shouldDeleteProductSuccessfully() throws Exception {
        UUID id = savedProducts.getFirst().getId();

        mockMvc.perform(delete("/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("The requested resource could not be found."));

        mockMvc.perform(get("/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Samsung Galaxy"));

        assertTrue(productJpaRepository.findById(id).isEmpty());
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting non-existent product")
    void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/products/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("The requested resource could not be found."));
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting already deleted product")
    void shouldReturn404WhenDeletingAlreadyDeletedProduct() throws Exception {
        UUID id = savedProducts.getFirst().getId();

        mockMvc.perform(delete("/v1/products/{id}", id))
                .andExpect(status().isNoContent());

        mockMvc.perform(delete("/v1/products/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("The requested resource could not be found."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when deleting with invalid UUID")
    void shouldReturn400WhenDeletingWithInvalidUUID() throws Exception {
        mockMvc.perform(delete("/v1/products/{id}", "invalid-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid value provided."));
    }
}
