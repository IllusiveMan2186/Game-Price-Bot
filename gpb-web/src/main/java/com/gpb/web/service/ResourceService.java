package com.gpb.web.service;

public interface ResourceService {

    /**
     * Get game images from folder
     *
     * @param gameName game name
     * @return image in byte array
     */
    byte[] getGameImage(final String gameName);
}
