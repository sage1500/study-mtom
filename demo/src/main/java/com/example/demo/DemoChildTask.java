package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class DemoChildTask implements Runnable {

	private final Object  msg;

	@Override
	public void run() {
		log.debug("ChildTask START: msg={}", msg);
		
		try {
			Thread.sleep(15000);
		} catch (InterruptedException e) {
			//
		}
		
		log.debug("ChildTask END: msg={}", msg);
	}

	
}
