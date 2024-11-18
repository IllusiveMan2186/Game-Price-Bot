package com.gpb.web.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
public class FlywayManualConfig {

    @Value("${ADMIN_EMAIL}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD_HASH}")
    private String adminPasswordHash;

    @Bean
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .baselineOnMigrate(true) // Optional: baseline to avoid schema validation errors
                .baselineVersion("1")
                .placeholders(Map.of(
                        "ADMIN_EMAIL", adminEmail,
                        "ADMIN_PASSWORD_HASH", adminPasswordHash
                ))
                .load();
    }
}
