/**
 * A utility class that holds constant values used across the application.
 */
package com.gpb.common.util;

public class CommonConstants {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private CommonConstants() {
    }

    /**
     * Header name for API key authentication.
     */
    public static final String API_KEY_HEADER = "x-api-key";

    /**
     * Header name for identifying a basic user ID.
     */
    public static final String BASIC_USER_ID_HEADER = "basic-user-id";

    public static final String JPG_IMG_FILE_EXTENSION = ".jpg";

    /**
     * Kafka topic for email service events.
     */
    public static final String EMAIL_SERVICE_TOPIC = "gpb_email_event";

    /**
     * Kafka topic for email notifications.
     */
    public static final String EMAIL_NOTIFICATION_TOPIC = "gpb_email_notification";

    /**
     * Kafka topic for following a game event.
     */
    public static final String GAME_FOLLOW_TOPIC = "gpb_game_follow";

    /**
     * Kafka topic for unfollowing a game event.
     */
    public static final String GAME_UNFOLLOW_TOPIC = "gpb_game_unfollow";

    /**
     * Kafka topic for removing a game.
     */
    public static final String GAME_REMOVE_TOPIC = "gpb_game_remove";

    /**
     * Kafka topic for removing a game in store.
     */
    public static final String GAME_IN_STORE_REMOVE_TOPIC = "gpb_game_in_store_remove";

    /**
     * Kafka group ID for the GPB application.
     */
    public static final String GPB_KAFKA_GROUP_ID = "gpb";

    /**
     * Name sort param
     */
    public static final String NAME_SORT_PARAM = "name";

    /**
     * Price sort param
     */
    public static final String PRICE_SORT_PARAM = "gamesInShop.discountPrice";

    /**
     * Ascending sort direction
     */
    public static final String SORT_DIRECTION_ASCENDING = "ASC";

    /**
     * Descending sort direction
     */
    public static final String SORT_DIRECTION_DESCENDING = "DESC";

    /**
     * Pattern of sort string
     */
    public static final String SORT_PARAM_REGEX = String.format("((%s)|(%s))-(%s|%s)",
            CommonConstants.PRICE_SORT_PARAM,
            CommonConstants.NAME_SORT_PARAM,
            CommonConstants.SORT_DIRECTION_ASCENDING,
            CommonConstants.SORT_DIRECTION_DESCENDING);
}
