package com.example.rest.server;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Repository;

import com.example.api.rest.server.model.NewPetResource;
import com.example.api.rest.server.model.PetResource;

@Repository
public class MemoryPetRepository {

	private final Map<Long, PetResource> pets = new ConcurrentHashMap<>();
	private final AtomicLong nextId = new AtomicLong(1);
	
	public PetResource findById(long id) {
		return pets.get(id);
	}
	
	public List<PetResource> find(List<String> tags) {
		return pets.values().stream().filter(p -> (tags == null || tags.contains(p.getTag()))).toList();
	}
	
	public PetResource add(NewPetResource pet) {
		var p = new PetResource();
		p.setId(nextId.getAndIncrement());
		p.setName(pet.getName());
		p.setTag(pet.getTag());
		pets.put(p.getId(), p);
		return p;
	}
}
