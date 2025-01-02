package com.comp5348.storeapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;
import org.springframework.retry.annotation.EnableRetry;

@PropertySource("classpath:application-storeapp.properties")
@SpringBootApplication(scanBasePackages = "com.comp5348.storeapp")
@EnableRetry
public class StoreappApplication {

	public static void main(String[] args) {
		SpringApplication.run(StoreappApplication.class, args);
	}

}
