package com.example.rental.service;

import com.example.rental.model.Property;
import com.example.rental.model.Property.ApprovalStatus;
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

    public @NonNull Property add(@NonNull Property p) {
        p.setApprovalStatus(ApprovalStatus.PENDING);
        return repo.save(p);
    }

    public List<Property> allApproved() {
        return repo.findByApprovalStatus(ApprovalStatus.APPROVED);
    }

    public List<Property> allForAdmin() {
        return repo.findAll();
    }

    public List<Property> pendingForAdmin() {
        return repo.findByApprovalStatus(ApprovalStatus.PENDING);
    }

    public List<Property> available() {
        return repo.findByApprovalStatusAndAvailableTrue(ApprovalStatus.APPROVED);
    }

    public Optional<Property> findById(@NonNull Long id) { return repo.findById(id); }

    public @NonNull Property approve(@NonNull Long id) {
        Property p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        p.setApprovalStatus(ApprovalStatus.APPROVED);
        return repo.save(p);
    }

    public @NonNull Property reject(@NonNull Long id) {
        Property p = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        p.setApprovalStatus(ApprovalStatus.REJECTED);
        p.setAvailable(false);
        return repo.save(p);
    }
}

