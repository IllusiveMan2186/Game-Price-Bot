package com.gpb.telegram.util;

public class Consts {

    private Consts() {
    }

    public static final String PLEASE_CHECK_HELP = System.lineSeparator() +
            "Please start message available with command . " +
            "List of available commands could be seen by command /help";
    public static final String CANT_UNDERSTAND = "Cannot recognize message without command. " +
            PLEASE_CHECK_HELP;
    public static final String UNKNOWN_COMMAND = "Unknown command. " + PLEASE_CHECK_HELP;
    public static final String USER_EXISTING_FILTER = "USER_EXISTING";
}
