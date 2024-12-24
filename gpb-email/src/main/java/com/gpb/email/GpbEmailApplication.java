package com.gpb.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@SpringBootApplication
public class GpbEmailApplication {

	public static void main(String[] args) {
		SpringApplication.run(GpbEmailApplication.class, args);
	}

}
