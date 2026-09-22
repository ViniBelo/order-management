package br.com.devpasso.order_management.application.dto.command;

public record UpdateProductStockCommand(
        Integer stockQuantity
) {
    public UpdateProductStockCommand {
        validateStockQuantity(stockQuantity);
    }

    private void validateStockQuantity(Integer stockQuantity) {
        if (stockQuantity == null) {
            throw new IllegalArgumentException("StockQuantity cannot be null");
        }
        if (stockQuantity < 0) {
            throw new IllegalArgumentException("StockQuantity must be greater than or equal to zero");
        }
    }
}
