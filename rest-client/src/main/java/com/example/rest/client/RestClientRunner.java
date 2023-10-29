package com.example.rest.client;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.example.api.rest.client.api.PetsApi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestClientRunner implements ApplicationRunner {

	private final PetsApi petApi;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("★START");

		if (true) {
			var pets = petApi.listPets(100);
			log.info("Pets:");
			pets.forEach(p -> log.info("  pet: {}", p));
		} else {
			var executor = Executors.newFixedThreadPool(20);
			for (int i = 0; i < 20; i++) {
				executor.execute(() -> {
					for (int j = 0; j < 5; j++) {
						log.info("call start {}", j);
						try {
							petApi.listPets(100);
						} catch (Exception e) {
							log.info("★error", e);
						}
						log.info("call end {}", j);
					}
				});
			}
			executor.shutdown();
			executor.awaitTermination(300, TimeUnit.SECONDS);
		}

		log.info("★END");
	}

}
