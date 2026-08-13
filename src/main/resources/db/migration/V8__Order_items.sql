CREATE TABLE order_items (
                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                             order_id UUID NOT NULL,
                             product_id UUID NOT NULL,

                             product_name_snapshot VARCHAR(255) NOT NULL,
                             product_price_snapshot NUMERIC(12, 2) NOT NULL,
                             quantity INTEGER NOT NULL,
                             line_total NUMERIC(12, 2) NOT NULL,
                             created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             CONSTRAINT fk_order_items_order
                                 FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,

                             CONSTRAINT fk_order_items_product
                                 FOREIGN KEY (product_id) REFERENCES products(id)
);

CREATE INDEX idx_order_items_order ON order_items(order_id);

CREATE INDEX idx_order_items_product ON order_items(product_id);
