package com.example.rental.service;

import com.example.rental.model.Property;
import com.example.rental.repo.PropertyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {
    private final PropertyRepository repo;
    public PropertyService(PropertyRepository repo) { this.repo = repo; }

    public @NonNull Property add(@NonNull Property p) { return repo.save(p); }
    public List<Property> all() { return repo.findAll(); }
    public List<Property> available() { return repo.findByAvailableTrue(); }
    public Optional<Property> findById(@NonNull Long id) { return repo.findById(id); }

    public @NonNull Property book(@NonNull Long id) {
        Property p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        p.setAvailable(false);
        return repo.save(p);
    }
}

