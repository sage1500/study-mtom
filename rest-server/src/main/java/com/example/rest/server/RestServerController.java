package com.example.rest.server;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.api.rest.server.api.PetsApi;
import com.example.api.rest.server.model.NewPetResource;
import com.example.api.rest.server.model.PetResource;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RestServerController implements PetsApi {
	
	private final MemoryPetRepository pets;

	@PostConstruct
	public void init() {
		var cat = new NewPetResource();
		cat.setName("tama");
		cat.setTag("cat");
		pets.add(cat);
		
		var dog = new NewPetResource();
		dog.setName("pochi");
		dog.setTag("dog");
		pets.add(dog);
	}
	
	@Override
	public ResponseEntity<PetResource> findPetById(Long id) {
		var pet = pets.findById(id);
		return (pet != null) ? ResponseEntity.ok(pet) : ResponseEntity.notFound().build();
	}

	@Override
	public ResponseEntity<List<PetResource>> findPets(@Valid List<String> tags, @Valid Integer limit) {
		return ResponseEntity.ok(pets.find(tags));
	}

	@Override
	public ResponseEntity<PetResource> addPet(@Valid NewPetResource newPetResource) {
		var pet = pets.add(newPetResource);
		return ResponseEntity.ok(pet);
	}

}
