ALTER TABLE refresh_token
ADD CONSTRAINT uq_refresh_token_user UNIQUE (user_id);