package com.example.rental.repo;

import com.example.rental.model.Property;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByAvailableTrue();
}
