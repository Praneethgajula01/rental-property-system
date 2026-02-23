package com.example.rental.service;

import com.example.rental.dto.PagedResponse;
import com.example.rental.model.Property;
import com.example.rental.model.Role;
import com.example.rental.model.User;
import com.example.rental.repo.PropertyRepository;
import com.example.rental.repo.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PropertyService propertyService;

    @Test
    void add_setsPendingApprovalAndCreator() {
        User host = new User("host@test.com", "pass", "Host", Role.HOST);
        host.setId(7L);
        Property property = new Property("Skyline Flat", "Hyderabad", 3500.0);

        when(userRepository.findByEmail("host@test.com")).thenReturn(Optional.of(host));
        when(propertyRepository.save(any(Property.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Property saved = propertyService.add(property, "host@test.com");

        assertEquals(Property.ApprovalStatus.PENDING, saved.getApprovalStatus());
        assertEquals("host@test.com", saved.getCreatedBy().getEmail());
    }

    @Test
    void searchApproved_returnsPagedResponse() {
        Property one = new Property("Lake View", "Hyderabad", 2000.0);
        Page<Property> page = new PageImpl<>(List.of(one));

        when(propertyRepository.searchApproved(
                any(), any(), any(), any(), any(), any(Pageable.class)
        )).thenReturn(page);

        PagedResponse<Property> response = propertyService.searchApproved(
                "lake", 1000.0, 3000.0, true, 0, 6, "price", "asc"
        );

        assertEquals(1, response.getItems().size());
        assertEquals(0, response.getPage());
        assertEquals(1, response.getTotalItems());
    }
}
