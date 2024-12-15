-- Create Tables with Foreign Key Constraints Inline
CREATE TABLE IF NOT EXISTS basic_user (
    id BIGSERIAL NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS basic_user_notification_types (
    basic_user_id BIGINT NOT NULL,
    notification_types VARCHAR(255),
    CONSTRAINT FK_basic_user_to_notification_types FOREIGN KEY (basic_user_id) REFERENCES basic_user
);

CREATE TABLE IF NOT EXISTS game (
    id BIGSERIAL NOT NULL,
    is_followed BOOLEAN NOT NULL,
    name VARCHAR(255),
    type VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS game_genres (
    game_id BIGINT NOT NULL,
    genres SMALLINT,
    CONSTRAINT FK_game_to_genres FOREIGN KEY (game_id) REFERENCES game
);

CREATE TABLE IF NOT EXISTS game_in_shop (
    id BIGSERIAL NOT NULL,
    client_type VARCHAR(255),
    discount INTEGER NOT NULL,
    discount_date TIMESTAMP(6),
    discount_price NUMERIC(38, 2),
    is_available BOOLEAN NOT NULL,
    name_in_store VARCHAR(255),
    price NUMERIC(38, 2),
    url VARCHAR(255),
    game_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_game_to_shop FOREIGN KEY (game_id) REFERENCES game
);

CREATE TABLE IF NOT EXISTS user_game (
    user_id BIGINT NOT NULL,
    game_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, game_id),
    CONSTRAINT FK_user_to_game FOREIGN KEY (user_id) REFERENCES basic_user,
    CONSTRAINT FK_game_to_user FOREIGN KEY (game_id) REFERENCES game
);

CREATE TABLE IF NOT EXISTS account_linker (
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (token),
    CONSTRAINT FK_connector_to_user FOREIGN KEY (user_id) REFERENCES basic_user
);