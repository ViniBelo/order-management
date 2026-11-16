ALTER TABLE order_items
    ADD COLUMN deleted_at TIMESTAMP DEFAULT NULL;

CREATE UNIQUE INDEX uq_active_products_to_order
    ON order_items (product_id)
    WHERE deleted_at IS NULL;