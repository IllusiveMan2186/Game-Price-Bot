package com.gpb.web.configuration;

import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

/**
 * Create default admin user after tablets creation by jpa
 */
@Configuration
public class FlywayAfterJpaConfig implements CommandLineRunner {

    private final Flyway flyway;

    public FlywayAfterJpaConfig(Flyway flyway) {
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) {
        flyway.migrate();
    }
}