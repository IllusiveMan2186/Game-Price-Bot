package com.gpb.backend.configuration.security;

import com.gpb.backend.filter.JwtAuthFilter;
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
        http
                .cors().and()
                .exceptionHandling().authenticationEntryPoint(userAuthenticationEntryPoint)
                .and().addFilterBefore(new JwtAuthFilter(userAuthenticationProvider), BasicAuthenticationFilter.class)
                .csrf().disable()
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers(HttpMethod.POST, "/login", "/registration", "/activate").permitAll()
                        .requestMatchers(HttpMethod.POST, "/linker/set").permitAll()
                        .requestMatchers(HttpMethod.POST, "/user/resend/email/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/email/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/game/**").permitAll()
                        .anyRequest().authenticated());
        return http.build();
    }
}
