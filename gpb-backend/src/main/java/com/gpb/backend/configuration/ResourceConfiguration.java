package com.gpb.backend.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for resource-related properties.
 */
@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConfiguration {

    /**
     * The folder path where images are stored.
     */
    @Value("${IMAGE_FOLDER}")
    private String imageFolder;
}
