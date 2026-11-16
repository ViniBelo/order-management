CREATE TABLE order_items (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    order_id        UUID NOT NULL REFERENCES orders(id),
    product_id      UUID NOT NULL REFERENCES products(id),
    product_name    VARCHAR(255) NOT NULL,
    unit_price      NUMERIC (10, 2) NOT NULL,
    quantity        INTEGER NOT NULL CHECK (quantity > 0)
);

CREATE UNIQUE INDEX uq_order_product
    ON order_items (order_id, product_id);