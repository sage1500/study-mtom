package com.example.demo;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import lombok.extern.slf4j.Slf4j;

//@RequiredArgsConstructor
@Slf4j
public class DemoNonAnnoRunner extends DemoNonAnnoBase implements ApplicationRunner {
//	private final DemoNonAnnoProperties props;
//	@Autowired
//	private DemoNonAnnoProperties props;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("START {}", getClass().getSimpleName());
		log.info(" prop1: {}", props.getProp1());
		log.info(" prop2: {}", props.getProp2());
	}

}
