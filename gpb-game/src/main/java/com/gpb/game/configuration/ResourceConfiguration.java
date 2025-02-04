package com.gpb.game.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that holds resource-related properties.
 */
@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConfiguration {

    /**
     * The path to the image folder.
     */
    @Value("${IMAGE_FOLDER}")
    private String imageFolder;
}
