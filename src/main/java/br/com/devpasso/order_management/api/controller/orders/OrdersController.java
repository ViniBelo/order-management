package br.com.devpasso.order_management.api.controller.orders;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/orders")
@Tag(name = "Orders", description = "Operations related to orders")
public class OrdersController { }
