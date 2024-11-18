INSERT INTO basic_user (id) VALUES (1);

INSERT INTO web_user (id, email, failed_attempt, is_locked, is_activated, locale, password, role, basic_user_id)
VALUES (1, '${ADMIN_EMAIL}', 0, false, true, 'en','${ADMIN_PASSWORD_HASH}', 'ROLE_ADMIN', 1);