package br.com.devpasso.order_management.api.controller.orders;

import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderItemEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.OrderStatusEntity;
import br.com.devpasso.order_management.infrastructure.persistence.entity.ProductEntity;
import br.com.devpasso.order_management.infrastructure.persistence.repository.OrderJpaRepository;
import br.com.devpasso.order_management.infrastructure.persistence.repository.ProductJpaRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class OrdersControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    private ProductEntity savedProduct1;
    private ProductEntity savedProduct2;

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute("DELETE FROM order_items");
        jdbcTemplate.execute("DELETE FROM orders");
        jdbcTemplate.execute("DELETE FROM products");

        ProductEntity p1 = new ProductEntity();
        p1.changeName("Wireless Keyboard");
        p1.changePrice(new BigDecimal("120.00"));
        p1.changeStockQuantity(10);

        ProductEntity p2 = new ProductEntity();
        p2.changeName("Gaming Mouse");
        p2.changePrice(new BigDecimal("60.00"));
        p2.changeStockQuantity(5);

        savedProduct1 = productJpaRepository.save(p1);
        savedProduct2 = productJpaRepository.save(p2);
    }

    @Test
    @DisplayName("Should create order successfully and return 201 Created with Location header")
    void shouldCreateOrderSuccessfully() throws Exception {
        UUID customerId = UUID.randomUUID();
        String jsonPayload = """
                {
                    "customerId": "%s"
                }
                """.formatted(customerId);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(header().string("Location", matchesPattern("^/v1/orders/[a-f0-9\\-]+$")))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.items", hasSize(0)))
                .andExpect(jsonPath("$.total").value(0))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());

        assertEquals(1, orderJpaRepository.count());
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating order with null customerId")
    void shouldReturn400WhenCreatingOrderWithNullCustomerId() throws Exception {
        String jsonPayload = "{}";

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."))
                .andExpect(jsonPath("$.errors[0].field").value("customerId"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when creating order with malformed UUID")
    void shouldReturn400WhenCreatingOrderWithMalformedUuid() throws Exception {
        String jsonPayload = """
                {
                    "customerId": "not-a-valid-uuid"
                }
                """;

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @DisplayName("Should add item to order successfully and return 200 OK")
    void shouldAddItemToOrderSuccessfully() throws Exception {
        // Given: Create an order entity
        OrderEntity order = new OrderEntity();
        order.changeId(UUID.randomUUID());
        order.changeCustomerId(UUID.randomUUID());
        order.changeStatus(OrderStatusEntity.DRAFT);
        order.changeCreatedAt(Instant.now());
        OrderEntity savedOrder = orderJpaRepository.save(order);

        String jsonPayload = """
                {
                    "productId": "%s",
                    "quantity": 2
                }
                """.formatted(savedProduct1.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Wireless Keyboard"))
                .andExpect(jsonPath("$.unitPrice").value(120.00))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.subtotal").value(240.00));

        OrderEntity reloadedOrder = orderJpaRepository.findById(savedOrder.getId()).orElseThrow();
        assertEquals(1, reloadedOrder.getItems().size());
        assertEquals("Wireless Keyboard", reloadedOrder.getItems().getFirst().getProductName());
    }

    @Test
    @DisplayName("Should return 404 Not Found when adding item to non-existent order")
    void shouldReturn404WhenAddingItemToNonExistentOrder() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();
        String jsonPayload = """
                {
                    "productId": "%s",
                    "quantity": 1
                }
                """.formatted(savedProduct1.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", nonExistentOrderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Order not found for ID: " + nonExistentOrderId));
    }

    @Test
    @DisplayName("Should return 404 Not Found when adding non-existent product to order")
    void shouldReturn404WhenAddingNonExistentProductToOrder() throws Exception {
        OrderEntity order = new OrderEntity();
        order.changeId(UUID.randomUUID());
        order.changeCustomerId(UUID.randomUUID());
        order.changeStatus(OrderStatusEntity.DRAFT);
        order.changeCreatedAt(Instant.now());
        OrderEntity savedOrder = orderJpaRepository.save(order);

        UUID nonExistentProductId = UUID.randomUUID();
        String jsonPayload = """
                {
                    "productId": "%s",
                    "quantity": 1
                }
                """.formatted(nonExistentProductId);

        mockMvc.perform(post("/v1/orders/{id}/items", savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Product not found for ID: " + nonExistentProductId));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when adding item with insufficient stock")
    void shouldReturn400WhenAddingItemWithInsufficientStock() throws Exception {
        OrderEntity order = new OrderEntity();
        order.changeId(UUID.randomUUID());
        order.changeCustomerId(UUID.randomUUID());
        order.changeStatus(OrderStatusEntity.DRAFT);
        order.changeCreatedAt(Instant.now());
        OrderEntity savedOrder = orderJpaRepository.save(order);

        // Product2 has stock = 5, request quantity = 10
        String jsonPayload = """
                {
                    "productId": "%s",
                    "quantity": 10
                }
                """.formatted(savedProduct2.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail", containsString("Unsufficient stock quantity for: Gaming Mouse")));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when adding item with invalid quantity")
    void shouldReturn400WhenAddingItemWithInvalidQuantity() throws Exception {
        OrderEntity order = new OrderEntity();
        order.changeId(UUID.randomUUID());
        order.changeCustomerId(UUID.randomUUID());
        order.changeStatus(OrderStatusEntity.DRAFT);
        order.changeCreatedAt(Instant.now());
        OrderEntity savedOrder = orderJpaRepository.save(order);

        String jsonPayload = """
                {
                    "productId": "%s",
                    "quantity": 0
                }
                """.formatted(savedProduct1.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", savedOrder.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid request data."))
                .andExpect(jsonPath("$.errors[0].field").value("quantity"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when adding item with invalid order UUID")
    void shouldReturn400WhenAddingItemWithInvalidOrderUuid() throws Exception {
        String jsonPayload = """
                {
                    "productId": "%s",
                    "quantity": 1
                }
                """.formatted(savedProduct1.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", "invalid-uuid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid value provided."));
    }

    @Test
    @DisplayName("Should return order by ID and 200 OK")
    void shouldFindOrderByIdSuccessfully() throws Exception {
        UUID id = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        OrderStatusEntity orderStatusEntity = OrderStatusEntity.DRAFT;
        Instant createdAt = Instant.now();

        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(id);
        orderEntity.changeCustomerId(customerId);
        orderEntity.changeStatus(orderStatusEntity);
        orderEntity.changeCreatedAt(createdAt);

        OrderItemEntity item = new OrderItemEntity();
        item.changeOrder(orderEntity);
        item.changeProductId(savedProduct1.getId());
        item.changeProductName(savedProduct1.getName());
        item.changeUnitPrice(savedProduct1.getPrice());
        item.changeQuantity(2);

        orderEntity.addItem(item);

        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);

        mockMvc.perform(get("/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(savedOrder.getId().toString()))
                .andExpect(jsonPath("$.customerId").value(savedOrder.getCustomerId().toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].productName").value("Wireless Keyboard"))
                .andExpect(jsonPath("$.items[0].quantity").value(2))
                .andExpect(jsonPath("$.items[0].unitPrice").value(120.00))
                .andExpect(jsonPath("$.items[0].subtotal").value(240.00))
                .andExpect(jsonPath("$.total").value(240.00));
    }

    @Test
    @DisplayName("Should return 404 Not Found when order not found by ID")
    void shouldReturn404WhenOrderNotFoundById() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/v1/orders/{id}", nonExistentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Order not found for ID: " + nonExistentId));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when finding order with invalid UUID format")
    void shouldReturn400WhenFindingOrderWithInvalidUuidFormat() throws Exception {
        mockMvc.perform(get("/v1/orders/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid value provided."));
    }

    @Test
    @DisplayName("Should successfully execute complete order lifecycle: create order, add multiple items, retrieve full order with correct total")
    void shouldExecuteCompleteOrderLifecycle() throws Exception {
        UUID customerId = UUID.randomUUID();

        // 1. Create order
        String createOrderPayload = """
                {
                    "customerId": "%s"
                }
                """.formatted(customerId);

        String createOrderResponse = mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.total").value(0))
                .andReturn().getResponse().getContentAsString();

        // Extract orderId from response JSON or repository
        OrderEntity createdOrderEntity = orderJpaRepository.findAll().getFirst();
        UUID orderId = createdOrderEntity.getId();

        // 2. Add first item (2 x Wireless Keyboard @ 120.00 = 240.00)
        String addItem1Payload = """
                {
                    "productId": "%s",
                    "quantity": 2
                }
                """.formatted(savedProduct1.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addItem1Payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Wireless Keyboard"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.subtotal").value(240.00));

        // 3. Add second item (3 x Gaming Mouse @ 60.00 = 180.00)
        String addItem2Payload = """
                {
                    "productId": "%s",
                    "quantity": 3
                }
                """.formatted(savedProduct2.getId());

        mockMvc.perform(post("/v1/orders/{id}/items", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(addItem2Payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("Gaming Mouse"))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.subtotal").value(180.00));

        // 4. Retrieve complete order and verify total = 240.00 + 180.00 = 420.00
        mockMvc.perform(get("/v1/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId.toString()))
                .andExpect(jsonPath("$.customerId").value(customerId.toString()))
                .andExpect(jsonPath("$.status").value("DRAFT"))
                .andExpect(jsonPath("$.items", hasSize(2)))
                .andExpect(jsonPath("$.total").value(420.00));
    }

    @Test
    @DisplayName("Should list all orders with default DRAFT status and default pagination")
    void shouldListAllDraftOrdersByDefault() throws Exception {
        OrderEntity draftOrder1 = new OrderEntity();
        draftOrder1.changeId(UUID.randomUUID());
        draftOrder1.changeCustomerId(UUID.randomUUID());
        draftOrder1.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder1.changeCreatedAt(Instant.now());

        OrderEntity draftOrder2 = new OrderEntity();
        draftOrder2.changeId(UUID.randomUUID());
        draftOrder2.changeCustomerId(UUID.randomUUID());
        draftOrder2.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder2.changeCreatedAt(Instant.now().plusSeconds(1));

        OrderEntity confirmedOrder = new OrderEntity();
        confirmedOrder.changeId(UUID.randomUUID());
        confirmedOrder.changeCustomerId(UUID.randomUUID());
        confirmedOrder.changeStatus(OrderStatusEntity.CONFIRMED);
        confirmedOrder.changeCreatedAt(Instant.now());

        orderJpaRepository.saveAll(List.of(draftOrder1, draftOrder2, confirmedOrder));

        mockMvc.perform(get("/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].status").value("DRAFT"))
                .andExpect(jsonPath("$.content[1].status").value("DRAFT"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    @DisplayName("Should filter orders by specified status")
    void shouldFilterOrdersBySpecifiedStatus() throws Exception {
        OrderEntity draftOrder = new OrderEntity();
        draftOrder.changeId(UUID.randomUUID());
        draftOrder.changeCustomerId(UUID.randomUUID());
        draftOrder.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder.changeCreatedAt(Instant.now());

        OrderEntity confirmedOrder = new OrderEntity();
        confirmedOrder.changeId(UUID.randomUUID());
        confirmedOrder.changeCustomerId(UUID.randomUUID());
        confirmedOrder.changeStatus(OrderStatusEntity.CONFIRMED);
        confirmedOrder.changeCreatedAt(Instant.now());

        orderJpaRepository.saveAll(List.of(draftOrder, confirmedOrder));

        mockMvc.perform(get("/v1/orders")
                        .param("status", "CONFIRMED"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].status").value("CONFIRMED"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("Should support custom pagination and sorting for orders")
    void shouldSupportCustomPaginationAndSortingForOrders() throws Exception {
        OrderEntity draftOrder1 = new OrderEntity();
        draftOrder1.changeId(UUID.randomUUID());
        draftOrder1.changeCustomerId(UUID.randomUUID());
        draftOrder1.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder1.changeCreatedAt(Instant.now().minusSeconds(10));

        OrderEntity draftOrder2 = new OrderEntity();
        draftOrder2.changeId(UUID.randomUUID());
        draftOrder2.changeCustomerId(UUID.randomUUID());
        draftOrder2.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder2.changeCreatedAt(Instant.now());

        orderJpaRepository.saveAll(List.of(draftOrder1, draftOrder2));

        mockMvc.perform(get("/v1/orders")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "createdAt,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id").value(draftOrder2.getId().toString()))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2));
    }

    @Test
    @DisplayName("Should return empty content when no orders match status")
    void shouldReturnEmptyContentWhenNoOrdersMatchStatus() throws Exception {
        OrderEntity draftOrder = new OrderEntity();
        draftOrder.changeId(UUID.randomUUID());
        draftOrder.changeCustomerId(UUID.randomUUID());
        draftOrder.changeStatus(OrderStatusEntity.DRAFT);
        draftOrder.changeCreatedAt(Instant.now());

        orderJpaRepository.save(draftOrder);

        mockMvc.perform(get("/v1/orders")
                        .param("status", "DELIVERED"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when invalid order status is provided")
    void shouldReturn400WhenInvalidOrderStatusProvided() throws Exception {
        mockMvc.perform(get("/v1/orders")
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should successfully delete item from order and return 204 No Content")
    void shouldSuccessfullyDeleteItemFromOrder() throws Exception {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(UUID.randomUUID());
        orderEntity.changeCustomerId(UUID.randomUUID());
        orderEntity.changeStatus(OrderStatusEntity.DRAFT);
        orderEntity.changeCreatedAt(Instant.now());

        OrderItemEntity item1 = new OrderItemEntity();
        item1.changeProductId(savedProduct1.getId());
        item1.changeProductName(savedProduct1.getName());
        item1.changeUnitPrice(savedProduct1.getPrice());
        item1.changeQuantity(2);
        orderEntity.addItem(item1);

        OrderItemEntity item2 = new OrderItemEntity();
        item2.changeProductId(savedProduct2.getId());
        item2.changeProductName(savedProduct2.getName());
        item2.changeUnitPrice(savedProduct2.getPrice());
        item2.changeQuantity(1);
        orderEntity.addItem(item2);

        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);
        UUID item1Id = savedOrder.getItems().get(0).getId();
        UUID item2Id = savedOrder.getItems().get(1).getId();

        mockMvc.perform(delete("/v1/orders/{id}/items/{itemId}", savedOrder.getId(), item1Id))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/v1/orders/{id}", savedOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id").value(item2Id.toString()))
                .andExpect(jsonPath("$.items[0].productName").value("Gaming Mouse"))
                .andExpect(jsonPath("$.total").value(60.00));
    }

    @Test
    @DisplayName("Should return 404 Not Found when deleting item from non-existent order")
    void shouldReturn404WhenDeletingItemFromNonExistentOrder() throws Exception {
        UUID nonExistentOrderId = UUID.randomUUID();
        UUID itemId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/orders/{id}/items/{itemId}", nonExistentOrderId, itemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.detail").value("Order item not found for ID: " + nonExistentOrderId));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when deleting item with invalid UUID format")
    void shouldReturn400WhenDeletingItemWithInvalidUuidFormat() throws Exception {
        mockMvc.perform(delete("/v1/orders/{id}/items/{itemId}", "invalid-order-uuid", UUID.randomUUID()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid value provided."));

        mockMvc.perform(delete("/v1/orders/{id}/items/{itemId}", UUID.randomUUID(), "invalid-item-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Invalid value provided."));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when deleting item from non-draft order")
    void shouldReturn400WhenDeletingItemFromNonDraftOrder() throws Exception {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.changeId(UUID.randomUUID());
        orderEntity.changeCustomerId(UUID.randomUUID());
        orderEntity.changeStatus(OrderStatusEntity.CONFIRMED);
        orderEntity.changeCreatedAt(Instant.now());

        OrderItemEntity item = new OrderItemEntity();
        item.changeProductId(savedProduct1.getId());
        item.changeProductName(savedProduct1.getName());
        item.changeUnitPrice(savedProduct1.getPrice());
        item.changeQuantity(1);
        orderEntity.addItem(item);

        OrderEntity savedOrder = orderJpaRepository.save(orderEntity);
        UUID itemId = savedOrder.getItems().getFirst().getId();

        mockMvc.perform(delete("/v1/orders/{id}/items/{itemId}", savedOrder.getId(), itemId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Order is not in draft status"));
    }
}
