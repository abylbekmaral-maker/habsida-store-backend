CREATE TABLE store_delivery_settings (
                                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                         store_id UUID NOT NULL UNIQUE,

                                         delivery_enabled BOOLEAN NOT NULL DEFAULT FALSE,
                                         delivery_type VARCHAR(30) NOT NULL,

                                         minimum_order_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
                                         free_delivery_threshold NUMERIC(15, 2),

                                         max_distance_km NUMERIC(8, 2),

                                         city VARCHAR(100),
                                         zone VARCHAR(100),

                                         created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                         updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                         CONSTRAINT fk_delivery_settings_store
                                             FOREIGN KEY (store_id) REFERENCES stores(id)
                                                 ON DELETE CASCADE
);

CREATE TABLE store_delivery_areas (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                      delivery_settings_id UUID NOT NULL,

                                      city VARCHAR(100) NOT NULL,
                                      area_name VARCHAR(150) NOT NULL,
                                      delivery_fee NUMERIC(15, 2) NOT NULL DEFAULT 0,
                                      active BOOLEAN NOT NULL DEFAULT TRUE,

                                      created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                      CONSTRAINT fk_delivery_area_settings
                                          FOREIGN KEY (delivery_settings_id)
                                              REFERENCES store_delivery_settings(id)
                                              ON DELETE CASCADE,
                                      CONSTRAINT uq_delivery_settings_city_area 
                                      UNIQUE (delivery_settings_id, city, area_name)
);

CREATE TABLE store_delivery_restrictions (
                                             id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

                                             delivery_settings_id UUID NOT NULL,

                                             restriction_type VARCHAR(50) NOT NULL,
                                             restriction_value VARCHAR(255),
                                             description VARCHAR(255),
                                             active BOOLEAN NOT NULL DEFAULT TRUE,

                                             created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                             updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

                                             CONSTRAINT fk_delivery_restriction_settings
                                                 FOREIGN KEY (delivery_settings_id)
                                                     REFERENCES store_delivery_settings(id)
                                                     ON DELETE CASCADE
);

                                              CREATE INDEX idx_delivery_areas_settings_id 
                                                  ON store_delivery_areas(delivery_settings_id);

                                              CREATE INDEX idx_delivery_restr_settings_id 
                                                  ON store_delivery_restrictions(delivery_settings_id);
