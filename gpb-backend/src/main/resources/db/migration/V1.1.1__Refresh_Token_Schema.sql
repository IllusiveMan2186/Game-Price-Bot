CREATE TABLE refresh_token (
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    PRIMARY KEY (token),
    CONSTRAINT FK_refresh_token_to_user FOREIGN KEY (user_id) REFERENCES web_user ON DELETE CASCADE
);
