package com.example.rental.service;

import com.example.rental.model.Property;
import com.example.rental.repo.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {
    private final PropertyRepository repo;
    public PropertyService(PropertyRepository repo) { this.repo = repo; }

    public Property add(Property p) { return repo.save(p); }
    public List<Property> all() { return repo.findAll(); }
    public List<Property> available() { return repo.findByAvailableTrue(); }
    public Optional<Property> findById(Long id) { return repo.findById(id); }

    public Property book(Long id) {
        Property p = repo.findById(id).orElseThrow();
        p.setAvailable(false);
        return repo.save(p);
    }
}

