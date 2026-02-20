package com.example.rental.repo;

import com.example.rental.model.Booking;
import com.example.rental.model.Booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Booking> findAllByOrderByCreatedAtDesc();

    @Query("""
            SELECT COUNT(b) > 0 FROM Booking b
            WHERE b.property.id = :propertyId
              AND b.status IN :statuses
              AND b.checkInDate < :checkOutDate
              AND b.checkOutDate > :checkInDate
            """)
    boolean existsOverlappingBooking(
            @Param("propertyId") Long propertyId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<BookingStatus> statuses
    );
}
