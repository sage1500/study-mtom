package com.example.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

@Import(DemoNonAnnoProperties.class)
public class DemoNonAnnoConfig {
	@Bean
	public DemoNonAnnoRunner demoNonAnnonRunner(DemoNonAnnoProperties props) {
//		return new DemoNonAnnoRunner(props);
		return new DemoNonAnnoRunner();
	}
}
