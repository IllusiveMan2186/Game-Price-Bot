package com.gpb.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
@PropertySource("classpath:application.yml") // наш yaml файлик
public class GpbTelegramApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpbTelegramApplication.class, args);
	}

}
