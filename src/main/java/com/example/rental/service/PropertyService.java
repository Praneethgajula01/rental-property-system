package com.example.rental.service;

import com.example.rental.dto.PagedResponse;
import com.example.rental.model.Property;
import com.example.rental.model.Property.ApprovalStatus;
import com.example.rental.model.User;
import com.example.rental.repo.PropertyRepository;
import com.example.rental.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class PropertyService {
    private final PropertyRepository repo;
    private final UserRepository userRepository;

    public PropertyService(PropertyRepository repo, UserRepository userRepository) {
        this.repo = repo;
        this.userRepository = userRepository;
    }

    public @NonNull Property add(@NonNull Property p, String userEmail) {
        User host = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        p.setCreatedBy(host);
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

    public List<Property> listingsByHost(String userEmail) {
        User host = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return repo.findByCreatedByIdOrderByIdDesc(host.getId());
    }

    public PagedResponse<Property> searchApproved(
            String query,
            Double minPrice,
            Double maxPrice,
            Boolean available,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        String normalizedQuery = (query == null || query.isBlank()) ? null : query.trim();
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                Math.max(size, 1),
                resolveSort(sortBy, sortDir)
        );

        Page<Property> result = repo.searchApproved(
                ApprovalStatus.APPROVED,
                normalizedQuery,
                minPrice,
                maxPrice,
                available,
                pageable
        );
        return PagedResponse.from(result);
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

    private Sort resolveSort(String sortBy, String sortDir) {
        String safeSortBy = switch (sortBy == null ? "" : sortBy) {
            case "name" -> "name";
            case "location" -> "location";
            case "price" -> "price";
            default -> "id";
        };
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, safeSortBy);
    }
}

