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
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS user_activation (
    token VARCHAR(255) NOT NULL,
    user_id BIGINT,
    PRIMARY KEY (token),
    CONSTRAINT FK_activation_to_user FOREIGN KEY (user_id) REFERENCES web_user
);