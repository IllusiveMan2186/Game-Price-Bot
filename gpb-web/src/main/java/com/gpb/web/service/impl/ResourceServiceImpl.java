package com.gpb.web.service.impl;

import com.gpb.web.configuration.ResourceConfiguration;
import com.gpb.web.exception.GameImageNotFoundException;
import com.gpb.web.service.ResourceService;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;

import static com.gpb.web.util.Constants.JPG_IMG_FILE_EXTENSION;
import static com.gpb.web.util.Constants.PNG_IMG_FILE_EXTENSION;

@Log4j2
@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceConfiguration resourceConfiguration;

    public ResourceServiceImpl(ResourceConfiguration resourceConfiguration) {
        this.resourceConfiguration = resourceConfiguration;
    }

    public byte[] getGameImage(final String gameName) {
        String gameImageFullPath = resourceConfiguration.getImageFolder() + "/" + sanitizeFilename(gameName)
                + JPG_IMG_FILE_EXTENSION;
        try {
            InputStream in = new FileInputStream(gameImageFullPath);

            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            return getDefaultGameImage();
        }
    }

    private byte[] getDefaultGameImage() {
        String gameImageFullPath = resourceConfiguration.getImageFolder() + "/defaultImage" + PNG_IMG_FILE_EXTENSION;
        try {
            InputStream in = new FileInputStream(gameImageFullPath);
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            log.error(String.format("Not found image by path '%s'. Full message :'%s'", gameImageFullPath, e.getMessage()));
            throw new GameImageNotFoundException(e);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[:/]", "_");
    }
}
