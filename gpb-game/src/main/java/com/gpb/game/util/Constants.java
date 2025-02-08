package com.gpb.game.util;

public class Constants {

    private Constants() {
    }
    public static final String GAME_INFO_CHANGE_DAILY = "00 00 04 * * *";//4:00:00 AM every day
    public static final String GAME_INFO_CHANGE_WEAKLY = "00 10 04 * * 1";//4:10:00 AM every Monday

    public static final String ATTRIBUTE_HREF = "href";

    public static final long SEARCH_REQUEST_WAITING_TIME = 23_000;//23 seconds
}
