package com.gpb.telegram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
@EnableJpaRepositories
@PropertySource("classpath:application.yml")
public class GpbTelegramApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpbTelegramApplication.class, args);
	}

}
