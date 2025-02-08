package com.gpb.game.util;

import com.gpb.common.util.CommonConstants;
import lombok.extern.slf4j.Slf4j;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Utility class for image processing operations.
 */
@Slf4j
public class ImageUtils {

    /**
     * Crops a specified rectangular region from an image and saves it to a file.
     *
     * @param imageUrl The URL of the image to be cropped.
     * @param filePath The path where the cropped image should be saved.
     * @param x        The X coordinate of the upper-left corner of the cropping region.
     * @param y        The Y coordinate of the upper-left corner of the cropping region.
     * @param w        The width of the cropping region.
     * @param h        The height of the cropping region.
     * @throws IOException If an error occurs during reading, writing, or invalid input.
     */
    public static void cropImage(String imageUrl, String gameName, String imageFolder, int x, int y, int w, int h) {
        String filePath = imageFolder + "/" + gameName + CommonConstants.JPG_IMG_FILE_EXTENSION;

        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty.");
        }
        if (filePath == null || filePath.isEmpty()) {
            throw new IllegalArgumentException("File path cannot be null or empty.");
        }
        if (w <= 0 || h <= 0) {
            throw new IllegalArgumentException("Width and height must be positive values.");
        }

        BufferedImage image;
        try {
            image = ImageIO.read(new URL(imageUrl));
        } catch (MalformedURLException e) {
            log.error("Invalid URL format: '{}'. Exception:{}", imageUrl, e);
            return;
        } catch (IOException e) {
            log.error("Error loading image from URL: '{}'. Exception:{}", imageUrl, e);
            return;
        }

        if (x < 0 || y < 0 || x + w > image.getWidth() || y + h > image.getHeight()) {
            log.error("Cropping region is out of image bounds.");
        }

        BufferedImage croppedImage = image.getSubimage(x, y, w, h);

        File outputFile = new File(filePath);
        try {
            boolean success = ImageIO.write(croppedImage, "JPG", outputFile);
            if (!success) {
                log.error("Failed to write the image to the specified file.");
            }
        } catch (IOException e) {
            log.error("Error saving cropped image to file: '{}'. Exception:{}", filePath, e);
        }
    }


}

