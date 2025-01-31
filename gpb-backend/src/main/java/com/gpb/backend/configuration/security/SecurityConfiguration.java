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

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties
@AllArgsConstructor
public class SecurityConfiguration {

    private final UserAuthenticationProvider userAuthenticationProvider;

    private final UserAuthenticationEntryPoint userAuthenticationEntryPoint;

    @Bean
    @Order(1)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        AuthFilter authFilter = new AuthFilter(userAuthenticationProvider);

        http
                .cors().and()
                .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
                .and()
                .addFilterBefore(authFilter, BasicAuthenticationFilter.class)
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/login", "/registration", "/activate").permitAll()
                        .requestMatchers(HttpMethod.GET, "/check-auth").permitAll()
                        .requestMatchers(HttpMethod.POST, "/linker/set").permitAll()
                        .requestMatchers(HttpMethod.POST, "/logout-user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/resend/email/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/email/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/**").permitAll()
                        .anyRequest().authenticated())
                .logout(logout -> logout
                        .logoutUrl("/do-not-use-logout") // Avoid conflicts by setting a fake logout URL
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK); // Prevent redirect
                        })
                );

        return http.build();
    }

}
