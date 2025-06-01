package com.gpb.game.util;

public class Constants {

    private Constants() {
    }

    public static final String GAME_INFO_CHANGE_DAILY = "00 00 04 * * *";//4:00:00 AM every day
    public static final String GAME_INFO_CHANGE_WEAKLY = "00 20 04 * * 1";//4:10:00 AM every Monday

    public static final String PNG_IMG_FILE_EXTENSION = ".png";

    public static final String PNG_IMG_FILE_EXTENSION = ".png";

    public static final String ATTRIBUTE_HREF = "href";

    public static final long SEARCH_MAX_RESULT_PER_STORE = 6;
    public static final int THREAD_POOL_CORE_SIZE = 12;
    public static final int THREAD_POOL_MAX_SIZE = 16;
    public static final int THREAD_POOL_QUEUE_CAPACITY = 100;

    public static final String THREAD_POOL_NAME_PREFIX = "gpb-task-";
}
