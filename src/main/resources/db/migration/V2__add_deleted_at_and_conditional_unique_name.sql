ALTER TABLE products
    ADD COLUMN deleted_at TIMESTAMP DEFAULT NULL;

ALTER TABLE products
    DROP CONSTRAINT IF EXISTS products_name_key;

CREATE UNIQUE INDEX uq_products_name_active
    ON products (name)
    WHERE deleted_at IS NULL;
