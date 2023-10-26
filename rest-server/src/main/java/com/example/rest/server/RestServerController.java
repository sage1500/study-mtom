package com.example.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.ErrorResponseException;

import com.example.api.rest.server.api.PetsApi;
import com.example.api.rest.server.model.PetResource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RestServerController implements PetsApi {

	@Override
	public ResponseEntity<List<PetResource>> listPets(@Max(100) @Valid Integer limit) {
		log.debug("★pets");
		var pets = new ArrayList<PetResource>();
		
		var cat = new PetResource();
		cat.setId(1L);
		cat.setName("tama");
		cat.setTag("a");
		pets.add(cat);

		
		if (false) {
			log.info("★throw exception");
			throw new ErrorResponseException(HttpStatus.TOO_MANY_REQUESTS);
		} else if (true) {
			try {
				Thread.sleep(9000);
			} catch (InterruptedException e) {
				throw new ErrorResponseException(HttpStatus.TOO_MANY_REQUESTS);
			}
		}
		
		return ResponseEntity.ok(pets);
	}

}
