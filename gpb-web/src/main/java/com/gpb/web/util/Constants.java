package com.gpb.web.util;

public class Constants {

    private Constants() {
    }

    public static final String API_KEY_HEADER = "X-API-Key";
    public static final String BASIC_USER_ID_HEADER = "basic-user-id";
    public static final String ADMIN_ROLE = "ROLE_ADMIN";
    public static final String USER_ROLE = "ROLE_USER";
    public static final String JPG_IMG_FILE_EXTENSION = ".jpg";
    public static final String PNG_IMG_FILE_EXTENSION = ".png";
    public static final String EMAIL_SERVICE_TOPIC = "gpb_email_event";
    public static final String EMAIL_NOTIFICATION_TOPIC = "gpb_email_notification";
    public static final String GAME_FOLLOW_TOPIC = "gpb_game_follow";
    public static final String GAME_UNFOLLOW_TOPIC = "gpb_game_unfollow";
    public static final String GAME_REMOVE_TOPIC = "gpb_game_remove";
    public static final String GAME_IN_STORE_REMOVE_TOPIC = "gpb_game_in_store_remove";
    public static final String USER_SYNCHRONIZATION_ACCOUNTS_TOPIC = "gpb_user_synchronization_accounts";
    public static final String GPB_KAFKA_GROUP_ID = "gpb";
    public static final long SEARCH_REQUEST_WAITING_TIME = 30;
}
