package com.example.rest.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class RestServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(RestServerApplication.class, args);
	}
}
