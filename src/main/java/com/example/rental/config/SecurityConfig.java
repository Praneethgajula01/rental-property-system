package com.example.rental.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, AuthenticationProvider authenticationProvider) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.authenticationProvider = authenticationProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll() // Open for login/register
                .requestMatchers("/properties/admin/**").hasRole("ADMIN")
                .requestMatchers("/bookings/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/properties/host/my").hasAnyRole("HOST", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/bookings/host/my").hasAnyRole("HOST", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/properties/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/properties").hasAnyRole("ADMIN", "HOST")
                .requestMatchers(HttpMethod.POST, "/properties/*/approve").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/properties/*/reject").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/bookings").authenticated()
                .requestMatchers(HttpMethod.GET, "/bookings/my").authenticated()
                .requestMatchers(HttpMethod.POST, "/bookings/*/confirm").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/bookings/*/cancel").authenticated()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // Open for Swagger
                .anyRequest().authenticated() // All other requests need token
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
