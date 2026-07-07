CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    status VARCHAR(50) NOT NULL
);

CREATE TABLE customer_addresses (
    id UUID PRIMARY KEY,
    customer_id UUID NOT NULL REFERENCES customers(id) ON DELETE CASCADE,
    address_line VARCHAR(500) NOT NULL,
    is_default BOOLEAN DEFAULT FALSE
);