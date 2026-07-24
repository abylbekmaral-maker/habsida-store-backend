CREATE TABLE store_hours (
                             id UUID PRIMARY KEY,
                             store_id UUID NOT NULL,
                             day_of_week VARCHAR(20) NOT NULL,
                             open_time TIME,
                             close_time TIME,
                             is_closed BOOLEAN NOT NULL DEFAULT FALSE,
                             last_order_cutoff_time TIME,
                             created_at TIMESTAMP NOT NULL,
                             updated_at TIMESTAMP NOT NULL,

                             CONSTRAINT fk_store_hours_store
                                 FOREIGN KEY (store_id)
                                     REFERENCES stores(id)
                                     ON DELETE CASCADE,

                             CONSTRAINT uk_store_hours_store_day
                                 UNIQUE (store_id, day_of_week)
);

CREATE TABLE store_breaks (
                              id UUID PRIMARY KEY,
                              store_hour_id UUID NOT NULL,
                              start_time TIME NOT NULL,
                              end_time TIME NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_store_breaks_store_hour
                                  FOREIGN KEY (store_hour_id)
                                      REFERENCES store_hours(id)
                                      ON DELETE CASCADE
);

CREATE INDEX idx_store_hours_store_id
    ON store_hours(store_id);

CREATE INDEX idx_store_breaks_store_hour_id
    ON store_breaks(store_hour_id);