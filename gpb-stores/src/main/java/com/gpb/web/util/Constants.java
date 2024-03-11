package com.gpb.web.util;

public class Constants {

    private Constants() {
    }

    public static final String USER_ROLE = "ROLE_USER";
    public static final String JPG_IMG_FILE_EXTENSION = ".jpg";
    public static final String GAME_INFO_CHANGE_CHECKING_TIME = "0 04 00 * * *";//4 am
    public static final String EMAIL_SERVICE_TOPIC = "gpb_email_event";
    public static final String GAME_NAME_SEARCH_TOPIC = "gpb_game_name_search_request";
    public static final String GAME_URL_SEARCH_TOPIC = "gpb_game_url_search_request";
    public static final String GAME_SEARCH_RESPONSE_TOPIC = "gpb_game_search_response";
    public static final String GPB_KAFKA_GROUP_ID = "gpb";
    public static final String SEARCH_REPLY_LISTENER_CONTAINER_FACTORY = "searchReplyListenerContainerFactory";
    public static final String FOLLOW_REPLY_LISTENER_CONTAINER_FACTORY = "followReplyListenerContainerFactory";
}
