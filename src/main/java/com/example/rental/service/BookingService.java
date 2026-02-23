package com.example.rental.service;

import com.example.rental.dto.BookingRequest;
import com.example.rental.dto.BookingResponse;
import com.example.rental.model.Booking;
import com.example.rental.model.Booking.BookingStatus;
import com.example.rental.model.Property;
import com.example.rental.model.Property.ApprovalStatus;
import com.example.rental.model.User;
import com.example.rental.repo.BookingRepository;
import com.example.rental.repo.PropertyRepository;
import com.example.rental.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public BookingService(
            BookingRepository bookingRepository,
            PropertyRepository propertyRepository,
            UserRepository userRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    public BookingResponse createBooking(BookingRequest request, String userEmail) {
        if (request.getPropertyId() == null || request.getCheckInDate() == null || request.getCheckOutDate() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Property and booking dates are required");
        }
        if (!request.getCheckOutDate().isAfter(request.getCheckInDate())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Checkout must be after check-in");
        }
        if (request.getCheckInDate().isBefore(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Check-in date cannot be in the past");
        }

        Property property = propertyRepository.findById(request.getPropertyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Property not found"));
        if (property.getApprovalStatus() != ApprovalStatus.APPROVED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Property is not approved for booking");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean overlapExists = bookingRepository.existsOverlappingBooking(
                property.getId(),
                request.getCheckInDate(),
                request.getCheckOutDate(),
                List.of(BookingStatus.REQUESTED, BookingStatus.CONFIRMED)
        );
        if (overlapExists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Property already booked for selected dates");
        }

        long stayDays = ChronoUnit.DAYS.between(request.getCheckInDate(), request.getCheckOutDate());
        Booking booking = new Booking();
        booking.setProperty(property);
        booking.setUser(user);
        booking.setCheckInDate(request.getCheckInDate());
        booking.setCheckOutDate(request.getCheckOutDate());
        booking.setTotalAmount(stayDays * property.getPrice());
        booking.setStatus(BookingStatus.REQUESTED);

        return toResponse(bookingRepository.save(booking));
    }

    public List<BookingResponse> getMyBookings(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return bookingRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponse> getAllBookingsForAdmin() {
        return bookingRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<BookingResponse> getHostBookings(String hostUserEmail) {
        User host = userRepository.findByEmail(hostUserEmail)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return bookingRepository.findByPropertyCreatedByIdOrderByCreatedAtDesc(host.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public BookingResponse confirmBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cancelled booking cannot be confirmed");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        return toResponse(bookingRepository.save(booking));
    }

    public BookingResponse cancelBooking(Long bookingId, String userEmail, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));

        boolean isOwner = booking.getUser().getEmail().equalsIgnoreCase(userEmail);
        if (!isOwner && !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to cancel this booking");
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            return toResponse(booking);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    private BookingResponse toResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setPropertyId(booking.getProperty().getId());
        response.setPropertyName(booking.getProperty().getName());
        response.setPropertyLocation(booking.getProperty().getLocation());
        response.setUserEmail(booking.getUser().getEmail());
        response.setCheckInDate(booking.getCheckInDate());
        response.setCheckOutDate(booking.getCheckOutDate());
        response.setTotalAmount(booking.getTotalAmount());
        response.setStatus(booking.getStatus().name());
        response.setCreatedAt(booking.getCreatedAt());
        return response;
    }
}
