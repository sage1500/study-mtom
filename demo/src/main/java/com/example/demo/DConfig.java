package com.example.demo;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@AutoConfiguration
//@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE + 1)
@AutoConfigureBefore(PropertyPlaceholderAutoConfiguration.class)
@Import(DemoNonAnnoConfig.class)
@Slf4j
public class DConfig {
	public DConfig() {
		log.info("ctor: {}", this.getClass().getSimpleName());
	}
	
	@PostConstruct
	public void init() {
		log.info("init: {}", this.getClass().getSimpleName());
	}
	
	@Bean
	public static CBean dString() {
		log.info("dString");
		return new CBean();
	}

}
