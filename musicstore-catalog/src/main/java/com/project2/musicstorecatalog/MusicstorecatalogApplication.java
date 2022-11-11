package com.project2.musicstorecatalog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class MusicstorecatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicstorecatalogApplication.class, args);
	}

}
