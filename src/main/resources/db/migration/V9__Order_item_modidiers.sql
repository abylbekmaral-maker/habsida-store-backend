CREATE TABLE order_item_modifiers (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                      order_item_id UUID NOT NULL,
                                      modifier_option_id UUID NOT NULL,

                                      modifier_name_snapshot VARCHAR(255) NOT NULL,
                                      modifier_price_snapshot NUMERIC(12, 2) NOT NULL,
                                      created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_order_item_modifiers_order_item
                                          FOREIGN KEY (order_item_id) REFERENCES order_items(id) ON DELETE CASCADE,

                                      CONSTRAINT fk_order_item_modifiers_modifier_option
                                          FOREIGN KEY (modifier_option_id) REFERENCES modifier_options(id)
);

CREATE INDEX idx_order_item_modifiers_item ON order_item_modifiers(order_item_id);
CREATE INDEX idx_order_item_modifiers_option ON order_item_modifiers(modifier_option_id);
