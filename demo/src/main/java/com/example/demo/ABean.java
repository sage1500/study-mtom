package com.example.demo;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class ABean {
	@PostConstruct
	public void init() {
		log.info("init: {}", this.getClass().getSimpleName());
	}
}
