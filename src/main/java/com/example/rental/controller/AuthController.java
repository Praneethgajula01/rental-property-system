package com.example.rental.controller;

import com.example.rental.dto.AuthRequest;
import com.example.rental.dto.AuthResponse;
import com.example.rental.dto.RegisterRequest;
import com.example.rental.model.Role;
import com.example.rental.model.User;
import com.example.rental.repo.UserRepository;
import com.example.rental.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User registration and login")
public class AuthController {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthController(UserRepository repository, 
                          PasswordEncoder passwordEncoder, 
                          JwtService jwtService, 
                          AuthenticationManager authenticationManager) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Creates USER/HOST account and returns JWT token")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
        Role role = (request.getRole() == null) ? Role.USER : request.getRole();

        // Public signup must never create admin accounts.
        if (role == Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Admin registration is restricted");
        }
        
        var user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                role
        );
        repository.save(user);
        
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse(jwtToken, user.getEmail(), user.getName(), user.getRole().name())
        );
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(
                new AuthResponse(jwtToken, user.getEmail(), user.getName(), user.getRole().name())
        );
    }
}
