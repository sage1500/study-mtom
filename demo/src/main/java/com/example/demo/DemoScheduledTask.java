package com.example.demo;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemoScheduledTask implements Runnable {

	@Override
	public void run() {
		log.debug("Schedule START");
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}
		log.debug("Schedule END");
	}

}
