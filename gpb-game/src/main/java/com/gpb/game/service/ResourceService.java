package com.gpb.game.service;

/**
 * Service interface for handling image resources.
 */
public interface ResourceService {

    /**
     * Saves an image from a given URL to a specified file path without cropping.
     *
     * @param imageUrl The URL of the image to be saved.
     * @param gameName The name of the game (used for naming the file).
     */
    void saveImage(String imageUrl, String gameName);

    /**
     * Crops a specified rectangular region from an image and saves it to a file.
     *
     * @param imageUrl The URL of the image to be cropped.
     * @param gameName The name of the game (used for naming the file).
     * @param x        The X coordinate of the upper-left corner of the cropping region.
     * @param y        The Y coordinate of the upper-left corner of the cropping region.
     * @param w        The width of the cropping region.
     * @param h        The height of the cropping region.
     */
    void saveCroppedImage(String imageUrl, String gameName, int x, int y, int w, int h);

    /**
     * Retrieves the game image associated with the specified game name.
     *
     * @param gameName the name of the game for which to retrieve the image
     * @return a byte array containing the image data; if no image is found, a default image will be returned
     */
    byte[] getGameImage(final String gameName);
}
