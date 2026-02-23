package com.example.rental.controller;

import com.example.rental.dto.BookingRequest;
import com.example.rental.dto.BookingResponse;
import com.example.rental.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "Booking lifecycle APIs")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    @Operation(summary = "Create booking request")
    public ResponseEntity<BookingResponse> createBooking(
            @RequestBody BookingRequest request,
            Authentication authentication
    ) {
        return ResponseEntity.ok(bookingService.createBooking(request, authentication.getName()));
    }

    @GetMapping("/my")
    public ResponseEntity<List<BookingResponse>> myBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getMyBookings(authentication.getName()));
    }

    @GetMapping("/host/my")
    public ResponseEntity<List<BookingResponse>> hostBookings(Authentication authentication) {
        return ResponseEntity.ok(bookingService.getHostBookings(authentication.getName()));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<BookingResponse>> allBookingsForAdmin() {
        return ResponseEntity.ok(bookingService.getAllBookingsForAdmin());
    }

    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm booking (admin)")
    public ResponseEntity<BookingResponse> confirm(@PathVariable Long bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancel(Authentication authentication, @PathVariable Long bookingId) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, authentication.getName(), isAdmin));
    }
}
