DELETE FROM user_activation ;
DELETE FROM refresh_token ;
DELETE FROM web_user ;

ALTER TABLE web_user ALTER COLUMN id RESTART WITH 1;
