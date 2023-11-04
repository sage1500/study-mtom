package com.example.rest.client;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import com.example.api.rest.client.api.PetsClient;
import com.example.api.rest.client.model.ErrorResource;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestClientRunner implements ApplicationRunner {

	private final PetsClient petClient;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("★START");

		try {
			if (true) {
				var pets = petClient.findPets(null, 100);
				log.info("Pets:");
				pets.forEach(p -> log.info("  pet: {}", p));
			} else {
				var executor = Executors.newFixedThreadPool(20);
				for (int i = 0; i < 20; i++) {
					executor.execute(() -> {
						for (int j = 0; j < 5; j++) {
							log.info("call start {}", j);
							try {
								petClient.findPets(null, 100);
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
			
			petClient.findPetById(0L);
		} catch (RestClientResponseException e) {
			log.info("error: string={}", e.getResponseBodyAsString());
			log.info("error: object={}", e.getResponseBodyAs(ErrorResource.class));
		} catch (RestClientException e) {
			log.info("error:", e);
		}
		
		log.info("★END");
	}

}
