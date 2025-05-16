package com.gpb.game.service.impl;

import com.gpb.common.util.CommonConstants;
import com.gpb.game.configuration.ResourceConfiguration;
import com.gpb.game.exception.GameImageNotFoundException;
import com.gpb.game.service.ResourceService;
import com.gpb.game.util.Constants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@AllArgsConstructor
public class ResourceServiceImpl implements ResourceService {

    private final ResourceConfiguration resourceConfiguration;

    @Override
    public void saveImage(String imageUrl, String gameName) {
        try {
            validateInputs(imageUrl, gameName);
            String filePath = getFilePath(gameName);
            Path outputPath = Paths.get(filePath);
            BufferedImage image = loadImage(imageUrl);
            saveImage(image, outputPath);
        } catch (Exception e) {
            log.error("Failed to save image '{}'. Exception: {}", imageUrl, e.getMessage(), e);
        }
    }

    @Override
    public void saveCroppedImage(String imageUrl, String gameName, int x, int y, int w, int h) {
        try {
            validateInputs(imageUrl, gameName);
            String filePath = getFilePath(gameName);
            Path outputPath = Paths.get(filePath);
            BufferedImage image = loadImage(imageUrl);
            validateCroppingBounds(image, x, y, w, h);
            BufferedImage croppedImage = image.getSubimage(x, y, w, h);
            saveImage(croppedImage, outputPath);
        } catch (Exception e) {
            log.error("Failed to process image '{}'. Exception: {}", imageUrl, e.getMessage(), e);
        }
    }

    public byte[] getGameImage(final String gameName) {
        log.debug("Get game image by name '{}'", gameName);
        String gameImageFullPath = resourceConfiguration.getImageFolder() + "/" + sanitizeFilename(gameName)
                + CommonConstants.JPG_IMG_FILE_EXTENSION;
        try {
            log.debug("Trying get game image by path '{}'", gameImageFullPath);
            InputStream in = new FileInputStream(gameImageFullPath);

            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            log.error("Not found game image by path '{}'", gameImageFullPath);
            return getDefaultGameImage();
        }
    }

    private void validateInputs(String imageUrl, String gameName) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new IllegalArgumentException("Image URL cannot be null or empty.");
        }
        if (gameName == null || gameName.isEmpty()) {
            throw new IllegalArgumentException("Game name cannot be null or empty.");
        }
    }

    private BufferedImage loadImage(String imageUrl) throws IOException {
        try {
            return ImageIO.read(new URL(imageUrl));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid URL format: " + imageUrl, e);
        } catch (IOException e) {
            throw new IOException("Error loading image from URL: " + imageUrl, e);
        }
    }

    private void validateCroppingBounds(BufferedImage image, int x, int y, int w, int h) {
        if (x < 0 || y < 0 || x + w > image.getWidth() || y + h > image.getHeight()) {
            throw new IllegalArgumentException("Cropping region is out of image bounds.");
        }
    }

    private void saveImage(BufferedImage image, Path outputPath) throws IOException {
        try {
            Files.createDirectories(outputPath.getParent());
            boolean success = ImageIO.write(image, "PNG", outputPath.toFile());
            if (!success) {
                throw new IOException("Failed to write the image to the specified file.");
            }
            log.info("Image successfully saved at: {}", outputPath);
        } catch (IOException e) {
            throw new IOException("Error saving image to file: " + outputPath, e);
        }
    }

    private String getFilePath(String gameName) {
        return resourceConfiguration.getImageFolder() + "/" + gameName + CommonConstants.JPG_IMG_FILE_EXTENSION;
    }

    private byte[] getDefaultGameImage() {
        log.debug("Get game default image");
        String gameImageFullPath = resourceConfiguration.getImageFolder() + "/defaultImage" +
                Constants.PNG_IMG_FILE_EXTENSION;
        try {
            log.debug("Trying get game default image '{}'", gameImageFullPath);
            InputStream in = new FileInputStream(gameImageFullPath);
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            log.error("Not found image by path '{}'. Full message :'{}'", gameImageFullPath, e.getMessage());
            throw new GameImageNotFoundException(e);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[:/]", "_");
    }

}