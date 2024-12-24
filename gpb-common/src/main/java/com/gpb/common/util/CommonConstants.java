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
}
