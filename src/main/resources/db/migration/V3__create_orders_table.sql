CREATE TYPE ORDER_STATUS AS ENUM (
    'DRAFT',
    'CONFIRMED',
    'IN_DELIVERY',
    'DELIVERED',
    'CANCELLED'
);

CREATE TABLE orders (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    customer_id UUID NOT NULL,
    status      ORDER_STATUS NOT NULL DEFAULT 'DRAFT',
    created_at  TIMESTAMP NOT NULL DEFAULT now()
);