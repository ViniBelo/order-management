package br.com.devpasso.order_management.application.dto.command.orders;

public enum FilterOrdersByStatusCommand {
    DRAFT,
    CONFIRMED,
    IN_DELIVERY,
    DELIVERED,
    CANCELLED
}