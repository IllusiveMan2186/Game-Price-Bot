package com.gpb.backend.service;

/**
 * Service interface for handling resource-related operations.
 */
public interface ResourceService {

    /**
     * Retrieves the game image associated with the specified game name.
     *
     * @param gameName the name of the game for which to retrieve the image
     * @return a byte array containing the image data; if no image is found, an empty array or an appropriate exception may be returned/raised
     */
    byte[] getGameImage(final String gameName);
}
