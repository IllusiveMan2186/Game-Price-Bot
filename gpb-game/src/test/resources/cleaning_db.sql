DELETE FROM user_game ;
DELETE FROM account_linker ;
DELETE FROM basic_user_notification_types ;
DELETE FROM basic_user ;
DELETE FROM game_in_shop ;
DELETE FROM game_genres ;
DELETE FROM game ;

ALTER TABLE basic_user ALTER COLUMN id RESTART WITH 1;
ALTER TABLE game_in_shop ALTER COLUMN id RESTART WITH 1;
ALTER TABLE game ALTER COLUMN id RESTART WITH 1;
