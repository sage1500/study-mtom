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

		if (false) {
			var pets = petApi.listPets(100);
			log.info("Pets:");
			pets.forEach(p -> log.info("  pet: {}", p));
		} else {
			var executor = Executors.newFixedThreadPool(20);
			for (int i = 0; i < 14; i++) {
				executor.execute(() -> {
					log.info("call start");
					try {
						petApi.listPets(100);
					} catch (Exception e) {
						log.info("★error", e);
					}
					log.info("call end");
				});
			}
			executor.shutdown();
			executor.awaitTermination(300, TimeUnit.SECONDS);
		}

		log.info("★END");
	}

}
