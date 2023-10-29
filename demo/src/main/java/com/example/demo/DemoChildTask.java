package com.example.demo;

import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DemoChildTask implements Runnable {

	private final Object  msg;
	private final DemoMapper mapper;

	@Override
	@Transactional
	public void run() {
		log.debug("ChildTask START: msg={}", msg);
		
		try {
			
			var count = mapper.count();
			log.debug("count = {}", count);
			
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			//
		}
		
		log.debug("ChildTask END: msg={}", msg);
	}

	
}
