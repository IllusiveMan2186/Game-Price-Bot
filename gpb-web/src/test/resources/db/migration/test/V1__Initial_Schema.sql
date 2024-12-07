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

CREATE TABLE IF NOT EXISTS web_user (
    id BIGSERIAL NOT NULL,
    email VARCHAR(255),
    failed_attempt INTEGER NOT NULL,
    is_activated BOOLEAN NOT NULL,
    is_locked BOOLEAN NOT NULL,
    locale VARCHAR(255),
    lock_time TIMESTAMP(6),
    password VARCHAR(255),
    role VARCHAR(255),
    basic_user_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT FK_user_to_basic_user FOREIGN KEY (basic_user_id) REFERENCES basic_user
);

CREATE TABLE IF NOT EXISTS user_activation (
    token VARCHAR(255) NOT NULL,
    user_id BIGINT,
    PRIMARY KEY (token),
    CONSTRAINT FK_activation_to_user FOREIGN KEY (user_id) REFERENCES web_user
);

CREATE TABLE IF NOT EXISTS web_messenger_connector (
    token VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (token),
    CONSTRAINT FK_connector_to_user FOREIGN KEY (user_id) REFERENCES basic_user
);

-- Insert a basic user for the admin
INSERT INTO basic_user (id)
SELECT 1
WHERE NOT EXISTS (SELECT 1 FROM basic_user WHERE id = 1);

-- Insert the admin web user
INSERT INTO web_user (
    id, email, password, locale, role, is_activated, is_locked, failed_attempt, basic_user_id
)
SELECT
    1,
    '${ADMIN_EMAIL}', -- Replace with the actual admin email or use Flyway placeholders
    '${ADMIN_PASSWORD_HASH}', -- Replace with a hashed password
    'en',
    'ROLE_ADMIN',
    TRUE,
    FALSE,
    0,
    1
WHERE NOT EXISTS (SELECT 1 FROM web_user WHERE id = 1);


