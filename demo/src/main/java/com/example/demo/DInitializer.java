package com.example.demo;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{

	@Getter
	private static ApplicationContext applicationContext;

	@Getter
	private static Environment environment;
	
	
	@Override
	public void initialize(ConfigurableApplicationContext applicationContext) {
		log.info("init: {}", this.getClass().getSimpleName());
		DInitializer.applicationContext = applicationContext;
		DInitializer.environment = applicationContext.getEnvironment();
	}

}
