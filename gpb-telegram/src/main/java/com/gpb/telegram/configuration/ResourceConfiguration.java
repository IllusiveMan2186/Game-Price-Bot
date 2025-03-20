package com.gpb.telegram.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceConfiguration {

    @Value("${IMAGE_FOLDER}")
    private String imageFolder;

    @Value("${FRONT_SERVICE_URL}")
    private String frontendServiceUrl;
}
