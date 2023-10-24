package com.example.rest.client;

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
		
		var pets = petApi.listPets(100);
		log.info("Pets:");
		pets.forEach(p -> log.info("  pet: {}", p));
		
		log.info("★END");
	}

}
