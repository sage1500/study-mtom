package com.example.rest.server;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import com.example.api.rest.server.api.PetsApi;
import com.example.api.rest.server.model.PetResource;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;

@Controller
public class RestServerController implements PetsApi {

	@Override
	public ResponseEntity<List<PetResource>> listPets(@Max(100) @Valid Integer limit) {
		var pets = new ArrayList<PetResource>();
		
		var cat = new PetResource();
		cat.setId(1L);
		cat.setName("tama");
		cat.setTag("a");
		pets.add(cat);
		
		return ResponseEntity.ok(pets);
	}

}
