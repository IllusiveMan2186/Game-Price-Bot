package com.gpb.backend.service.impl;

import com.gpb.backend.configuration.ResourceConfiguration;
import com.gpb.backend.exception.GameImageNotFoundException;
import com.gpb.backend.service.ResourceService;
import com.gpb.backend.util.Constants;
import com.gpb.common.util.CommonConstants;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.InputStream;

@Log4j2
@Service
public class ResourceServiceImpl implements ResourceService {

    private final ResourceConfiguration resourceConfiguration;

    public ResourceServiceImpl(ResourceConfiguration resourceConfiguration) {
        this.resourceConfiguration = resourceConfiguration;
    }

    public byte[] getGameImage(final String gameName) {
        String gameImageFullPath = resourceConfiguration.getImageFolder() + "/" + sanitizeFilename(gameName)
                + CommonConstants.JPG_IMG_FILE_EXTENSION;
        try {
            InputStream in = new FileInputStream(gameImageFullPath);

            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            return getDefaultGameImage();
        }
    }

    private byte[] getDefaultGameImage() {
        String gameImageFullPath = resourceConfiguration.getImageFolder() + "/defaultImage" + Constants.PNG_IMG_FILE_EXTENSION;
        try {
            InputStream in = new FileInputStream(gameImageFullPath);
            return IOUtils.toByteArray(in);
        } catch (Exception e) {
            log.error(String.format("Not found image by path '{}'. Full message :'{}'", gameImageFullPath, e.getMessage()));
            throw new GameImageNotFoundException(e);
        }
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[:/]", "_");
    }
}
