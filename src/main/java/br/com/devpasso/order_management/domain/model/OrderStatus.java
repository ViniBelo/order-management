package br.com.devpasso.order_management.domain.model;

public enum OrderStatus {
    DRAFT, CONFIRMED, IN_DELIVERY, DELIVERED, CANCELLED;

    public boolean canTransitionTo(OrderStatus next) {
        return switch (this) {
            case DRAFT -> next == CONFIRMED || next == CANCELLED;
            case CONFIRMED -> next == IN_DELIVERY || next == CANCELLED;
            case IN_DELIVERY -> next == DELIVERED || next == CANCELLED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
