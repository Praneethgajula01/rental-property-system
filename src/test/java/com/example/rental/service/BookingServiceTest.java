package com.example.rental.service;

import com.example.rental.dto.BookingRequest;
import com.example.rental.dto.BookingResponse;
import com.example.rental.model.Booking;
import com.example.rental.model.Property;
import com.example.rental.model.Role;
import com.example.rental.model.User;
import com.example.rental.repo.BookingRepository;
import com.example.rental.repo.PropertyRepository;
import com.example.rental.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    @Test
    void createBooking_createsRequestedBookingWithTotal() {
        Property property = new Property("Lake View", "Hyderabad", 2000.0);
        property.setId(1L);
        property.setApprovalStatus(Property.ApprovalStatus.APPROVED);

        User user = new User("buyer@test.com", "pass", "Buyer", Role.USER);
        user.setId(2L);

        BookingRequest request = new BookingRequest();
        request.setPropertyId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(4));

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(bookingRepository.existsOverlappingBooking(anyLong(), any(), any(), anyList())).thenReturn(false);
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setId(99L);
            return booking;
        });

        BookingResponse response = bookingService.createBooking(request, "buyer@test.com");

        assertEquals("REQUESTED", response.getStatus());
        assertEquals(6000.0, response.getTotalAmount());
        assertEquals(1L, response.getPropertyId());
    }

    @Test
    void createBooking_whenOverlapExists_throwsConflict() {
        Property property = new Property("Lake View", "Hyderabad", 2000.0);
        property.setId(1L);
        property.setApprovalStatus(Property.ApprovalStatus.APPROVED);
        User user = new User("buyer@test.com", "pass", "Buyer", Role.USER);

        BookingRequest request = new BookingRequest();
        request.setPropertyId(1L);
        request.setCheckInDate(LocalDate.now().plusDays(1));
        request.setCheckOutDate(LocalDate.now().plusDays(3));

        when(propertyRepository.findById(1L)).thenReturn(Optional.of(property));
        when(userRepository.findByEmail("buyer@test.com")).thenReturn(Optional.of(user));
        when(bookingRepository.existsOverlappingBooking(anyLong(), any(), any(), anyList())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> bookingService.createBooking(request, "buyer@test.com"));
    }

    @Test
    void cancelBooking_whenNotOwnerAndNotAdmin_throwsForbidden() {
        User owner = new User("owner@test.com", "pass", "Owner", Role.USER);
        Booking booking = new Booking();
        booking.setId(55L);
        booking.setUser(owner);
        booking.setStatus(Booking.BookingStatus.REQUESTED);

        when(bookingRepository.findById(55L)).thenReturn(Optional.of(booking));

        assertThrows(ResponseStatusException.class, () -> bookingService.cancelBooking(55L, "other@test.com", false));
        verify(bookingRepository, never()).save(any());
    }
}
