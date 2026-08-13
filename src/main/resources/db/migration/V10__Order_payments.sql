CREATE TABLE order_payments (
                                id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                order_id UUID NOT NULL UNIQUE,

                                method VARCHAR(30) NOT NULL,
                                status VARCHAR(30) NOT NULL,
                                paid_at TIMESTAMP,

                                provider VARCHAR(100),
                                transaction_id VARCHAR(255),

                                created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                CONSTRAINT fk_order_payments_order
                                    FOREIGN KEY (order_id) REFERENCES orders(id)
                                        ON DELETE CASCADE
);