package com.gpb.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableJpaRepositories
@EnableScheduling
@EnableMethodSecurity
public class GpbWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpbWebApplication.class, args);
	}

}
