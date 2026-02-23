package com.example.rental.repo;

import com.example.rental.model.Property;
import com.example.rental.model.Property.ApprovalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByAvailableTrue();
    List<Property> findByApprovalStatus(ApprovalStatus approvalStatus);
    List<Property> findByApprovalStatusAndAvailableTrue(ApprovalStatus approvalStatus);
    List<Property> findByCreatedByIdOrderByIdDesc(Long createdById);
}
