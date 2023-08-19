package com.wadajo.turismomadrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class TurismomadridApplication {

	public static void main(String[] args) {
		SpringApplication.run(TurismomadridApplication.class, args);
	}

}
