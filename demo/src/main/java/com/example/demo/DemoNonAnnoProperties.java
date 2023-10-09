package com.example.demo;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;

@Data
public class DemoNonAnnoProperties {
	@Value("${demo.prop1:no-prop1}")
	private String prop1;
	
	@Value("${demo.prop2:no-prop2}")
	private String prop2;
	
}
