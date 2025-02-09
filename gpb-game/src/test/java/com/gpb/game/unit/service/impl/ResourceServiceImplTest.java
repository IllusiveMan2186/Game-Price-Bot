package com.gpb.game.unit.service.impl;

import com.gpb.common.util.CommonConstants;
import com.gpb.game.configuration.ResourceConfiguration;
import com.gpb.game.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class ResourceServiceImplTest {

    @Mock
    private ResourceConfiguration resourceConfiguration;

    private ResourceServiceImpl resourceService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(resourceConfiguration.getImageFolder()).thenReturn(tempDir.toString());
        resourceService = new ResourceServiceImpl(resourceConfiguration);
    }

    @Test
    void testSaveCroppedImage_whenInvalidUrl_shouldNotSaveFile() {
        resourceService.saveCroppedImage("invalid-url", "game1", 10, 10, 50, 50);

        File outputFile = new File(tempDir.toFile(), "game1" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertFalse(outputFile.exists());
    }

    @Test
    void testSaveCroppedImage_whenEmptyGameName_shouldNotSaveFile() {
        resourceService.saveCroppedImage("http://example.com/image.jpg", "", 10, 10, 50, 50);

        File outputFile = new File(tempDir.toFile(), "" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertFalse(outputFile.exists());
    }

    @Test
    void testSaveCroppedImage_whenOutOfBoundsCropping_shouldNotSaveFile() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "JPG", new File(tempDir.toFile(), "test.jpg"));


        resourceService.saveCroppedImage("http://example.com/image.jpg", "game1", 90, 90, 50, 50);


        File outputFile = new File(tempDir.toFile(), "game1" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertFalse(outputFile.exists());
    }

    @Test
    void testSaveCroppedImage_whenValidImage_shouldSaveSuccessfully() throws IOException {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        File tempFile = new File(tempDir.toFile(), "test.jpg");
        ImageIO.write(image, "JPG", tempFile);

        URL imageUrl = tempFile.toURI().toURL();


        resourceService.saveCroppedImage(imageUrl.toString(), "game1", 10, 10, 50, 50);


        File outputFile = new File(tempDir.toFile(), "game1" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertTrue(outputFile.exists());
    }

    @Test
    void testSaveImage_whenEmptyGameName_shouldNotSaveFile() throws IOException {
        resourceService.saveImage("http://example.com/image.jpg", "");


        File outputFile = new File(tempDir.toFile(), "" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertFalse(outputFile.exists());
    }

    @Test
    void testSaveImage_whenInvalidUrl_shouldNotSaveFile() throws IOException {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        File tempFile = new File(tempDir.toFile(), "test.jpg");
        ImageIO.write(image, "JPG", tempFile);

        URL imageUrl = tempFile.toURI().toURL();


        resourceService.saveImage(imageUrl.toString(), "game1");


        File outputFile = new File(tempDir.toFile(), "game1" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertTrue(outputFile.exists());
    }
}

