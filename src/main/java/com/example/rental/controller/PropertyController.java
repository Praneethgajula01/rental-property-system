package com.example.rental.controller;

import com.example.rental.model.Property;
import com.example.rental.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/properties")
public class PropertyController {
    private final PropertyService service;
    public PropertyController(PropertyService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Property> add(@RequestBody Property p){
        return ResponseEntity.ok(service.add(p));
    }

    @GetMapping
    public ResponseEntity<List<Property>> all(){ return ResponseEntity.ok(service.all()); }

    @GetMapping("/available")
    public ResponseEntity<List<Property>> available(){ return ResponseEntity.ok(service.available()); }

    @PostMapping("/{id}/book")
    public ResponseEntity<Property> book(@PathVariable Long id){
        return ResponseEntity.ok(service.book(id));
    }
}

