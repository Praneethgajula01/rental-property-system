package com.example.rental.repo;

import com.example.rental.model.Property;
import com.example.rental.model.Property.ApprovalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface PropertyRepository extends JpaRepository<Property, Long> {
    List<Property> findByAvailableTrue();
    List<Property> findByApprovalStatus(ApprovalStatus approvalStatus);
    List<Property> findByApprovalStatusAndAvailableTrue(ApprovalStatus approvalStatus);
    List<Property> findByCreatedByIdOrderByIdDesc(Long createdById);

    @Query("""
            SELECT p FROM Property p
            WHERE p.approvalStatus = :approvalStatus
              AND (:query IS NULL OR
                  LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR
                  LOWER(p.location) LIKE LOWER(CONCAT('%', :query, '%')))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
              AND (:available IS NULL OR p.available = :available)
            """)
    Page<Property> searchApproved(
            @Param("approvalStatus") ApprovalStatus approvalStatus,
            @Param("query") String query,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("available") Boolean available,
            Pageable pageable
    );
}
