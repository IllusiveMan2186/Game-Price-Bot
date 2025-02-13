package com.gpb.backend.configuration.security;

import com.gpb.backend.filter.AuthFilter;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

/**
 * Configures the application's security settings .
 */
@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@AllArgsConstructor
public class SecurityConfiguration {

    private final UserAuthenticationProvider userAuthenticationProvider;
    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;

    /**
     * Configures the security filter chain for the application.
     * <p>
     * This filter chain configures CORS, disables CSRF protection, adds a custom authentication filter,
     * sets up endpoint-specific access rules, and defines a custom logout behavior to avoid conflicts.
     * </p>
     *
     * @param http the {@link HttpSecurity} object used to build the security filter chain
     * @return the configured {@link SecurityFilterChain}
     * @throws Exception if an error occurs while configuring the filter chain
     */
    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthFilter authFilter = new AuthFilter(userAuthenticationProvider);

        http
                .cors().and()
                .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint).and()
                .addFilterBefore(authFilter, BasicAuthenticationFilter.class)
                .csrf().disable()
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers(HttpMethod.POST, "/login", "/registration", "/activate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/refresh-token").permitAll()
                        .requestMatchers(HttpMethod.POST, "/linker/set").permitAll()
                        .requestMatchers(HttpMethod.POST, "/logout-user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/resend/email/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/email/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/**").permitAll()
                        .anyRequest().authenticated())
                // Configure a custom logout handler to avoid conflicts with default logout processing.
                .logout(logout -> logout
                        // Set a non-functional logout URL to prevent Spring Security from applying its default behavior.
                        .logoutUrl("/do-not-use-logout")
                        // On logout success, simply set the response status to 200 OK without a redirect.
                        .logoutSuccessHandler((request, response, authentication) ->
                                response.setStatus(HttpServletResponse.SC_OK))
                );

        return http.build();
    }
}
