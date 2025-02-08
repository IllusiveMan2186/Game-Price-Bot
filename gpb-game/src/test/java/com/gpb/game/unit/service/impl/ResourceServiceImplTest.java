package com.gpb.game.unit.service.impl;

import com.gpb.common.util.CommonConstants;
import com.gpb.game.configuration.ResourceConfiguration;
import com.gpb.game.service.impl.ResourceServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testCropImage_InvalidUrl_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                resourceService.cropImage("invalid-url", "game1", 10, 10, 50, 50));
    }

    @Test
    void testCropImage_EmptyGameName_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                resourceService.cropImage("http://example.com/image.jpg", "", 10, 10, 50, 50));
    }

    @Test
    void testCropImage_NegativeDimensions_ShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
                resourceService.cropImage("http://example.com/image.jpg", "game1", 10, 10, -50, 50));
    }

    @Test
    void testCropImage_OutOfBoundsCropping_ShouldThrowException() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageIO.write(image, "JPG", new File(tempDir.toFile(), "test.jpg"));

        assertThrows(IllegalArgumentException.class, () ->
                resourceService.cropImage("http://example.com/image.jpg", "game1", 90, 90, 50, 50));
    }

    @Test
    void testCropImage_ValidImage_ShouldSaveSuccessfully() throws IOException {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        File tempFile = new File(tempDir.toFile(), "test.jpg");
        ImageIO.write(image, "JPG", tempFile);

        URL imageUrl = tempFile.toURI().toURL();

        assertDoesNotThrow(() ->
                resourceService.cropImage(imageUrl.toString(), "game1", 10, 10, 50, 50));

        File outputFile = new File(tempDir.toFile(), "game1" + CommonConstants.JPG_IMG_FILE_EXTENSION);
        assertTrue(outputFile.exists());
    }
}

