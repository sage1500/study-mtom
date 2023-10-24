package com.example.rest.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(proxyBeanMethods = false)
public class RestClientApplication {
	public static void main(String[] args) {
//		new SpringApplicationBuilder(RestClientApplication.class)
//			.web(WebApplicationType.NONE)
//			.run(args);
		SpringApplication.run(RestClientApplication.class, args);
	}
}
