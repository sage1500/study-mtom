package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class CBean {
	@PostConstruct
	public void init() {
		log.info("init: {}", this.getClass().getSimpleName());
	}
}
