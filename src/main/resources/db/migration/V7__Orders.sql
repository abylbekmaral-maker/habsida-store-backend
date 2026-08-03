CREATE TABLE orders (
                        id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                        store_id UUID NOT NULL,
                        customer_id UUID NOT NULL,
                        customer_name VARCHAR(255) NOT NULL DEFAULT,
                        customer_phone VARCHAR(50) NOT NULL DEFAULT,

                        order_number VARCHAR(100) NOT NULL UNIQUE,
                        delivery_address VARCHAR(255),
                        type VARCHAR(30) NOT NULL,
                        status VARCHAR(30) NOT NULL,

                        accepted_at TIMESTAMP,
                        rejected_at TIMESTAMP,
                        reject_reason TEXT,
                        cancelled_at TIMESTAMP,
                        cancel_reason VARCHAR(255),
                        customer_note TEXT,

                        recipient_name VARCHAR(150),
                        recipient_phone VARCHAR(50),
                        delivery_address TEXT,
                        delivery_instructions TEXT,
                        delivery_area_name VARCHAR(150),
                        delivery_method VARCHAR(30),

                        subtotal NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        delivery_fee NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        discount_total NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        total NUMERIC(12, 2) NOT NULL DEFAULT 0,
                        currency VARCHAR(10) NOT NULL DEFAULT 'KRW',

                        created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                        CONSTRAINT fk_orders_store
                            FOREIGN KEY (store_id) REFERENCES stores(id),

                        CONSTRAINT fk_orders_customer
                            FOREIGN KEY (customer_id) REFERENCES customers(id)
);

CREATE INDEX idx_orders_store_status ON orders(store_id, status);
CREATE INDEX idx_orders_customer ON orders(customer_id);
