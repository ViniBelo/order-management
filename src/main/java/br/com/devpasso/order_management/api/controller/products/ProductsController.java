package br.com.devpasso.order_management.api.controller.products;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/products")
@Tag(name = "Products", description = "Operations about products")
public class ProductsController { }
