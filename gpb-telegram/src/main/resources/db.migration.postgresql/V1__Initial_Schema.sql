CREATE TABLE IF NOT EXISTS telegram_user (
    id BIGSERIAL NOT NULL,
    locale VARCHAR(255),
    telegram_id BIGINT,
    basic_user_id BIGINT UNIQUE,
    PRIMARY KEY (id)
);