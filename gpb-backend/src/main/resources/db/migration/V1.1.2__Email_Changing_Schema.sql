CREATE TABLE email_changing (
    id BIGSERIAL NOT NULL,
    user_id BIGINT NOT NULL,
    new_email VARCHAR(255) NOT NULL UNIQUE,
    old_email_token VARCHAR(255) NOT NULL UNIQUE,
    new_email_token VARCHAR(255) NOT NULL UNIQUE,
    is_new_email_confirmed BOOLEAN NOT NULL,
    is_old_email_confirmed BOOLEAN NOT NULL,
    expiration_time TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_email_changing_user FOREIGN KEY (user_id) REFERENCES web_user(id) ON DELETE CASCADE
);
