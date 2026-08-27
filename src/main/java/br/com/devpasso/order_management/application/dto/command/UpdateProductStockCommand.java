package br.com.devpasso.order_management.application.dto.command;

public record UpdateProductStockCommand(
        Integer stockQuantity
) { }
