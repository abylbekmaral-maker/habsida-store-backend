CREATE TABLE orders (
                        id UUID PRIMARY KEY,

                        store_id UUID NOT NULL,
                        customer_id UUID NOT NULL,

                        order_number VARCHAR(100) NOT NULL UNIQUE,
                        type VARCHAR(30) NOT NULL,
                        status VARCHAR(30) NOT NULL,

                        accepted_at TIMESTAMP,
                        rejected_at TIMESTAMP,
                        reject_reason TEXT,
                        customer_note TEXT,

                        subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        delivery_fee NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        discount_total NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        total NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        currency VARCHAR(10) NOT NULL DEFAULT 'KRW',

                        CONSTRAINT fk_orders_store
                            FOREIGN KEY (store_id) REFERENCES stores(id),

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_orders_store_status ON orders(store_id, status);
CREATE INDEX idx_orders_customer ON orders(customer_id);
