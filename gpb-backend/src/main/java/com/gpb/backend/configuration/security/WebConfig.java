package com.gpb.backend.configuration.security;

import com.gpb.backend.util.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Configuration class for setting up Cross-Origin Resource Sharing (CORS).
 * <p>
 * This configuration enables CORS for the backend application, allowing cross-origin requests
 * from the configured front-end service.
 * </p>
 */
@Configuration
public class WebConfig {

    private static final Long MAX_AGE = 3600L;

    /**
     * The URL of the front-end service that is allowed to make cross-origin requests.
     * Defaults to "http://localhost:3000" if not specified in the application properties.
     */
    @Value("${FRONT_SERVICE_URL:http://localhost:3000}")
    private String frontendServiceUrl;

    /**
     * Configures and returns a {@link CorsFilter} bean.
     * <p>
     * The configuration allows credentials and specifies allowed origins, headers, and HTTP methods.
     * It also sets the maximum age for which the preflight request can be cached.
     * </p>
     *
     * @return a configured {@link CorsFilter} bean for handling CORS.
     */
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(frontendServiceUrl);
        config.setAllowedHeaders(Arrays.asList(
                HttpHeaders.AUTHORIZATION,
                HttpHeaders.CONTENT_TYPE,
                HttpHeaders.ACCEPT,
                Constants.LINK_TOKEN_HEADER
        ));
        config.setAllowedMethods(Arrays.asList(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name()
        ));
        config.setMaxAge(MAX_AGE);

        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
