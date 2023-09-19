package com.gpb.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class GpbWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpbWebApplication.class, args);
	}

}
