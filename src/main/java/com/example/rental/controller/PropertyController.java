package com.example.rental.controller;

import com.example.rental.model.Property;
import com.example.rental.service.PropertyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.lang.NonNull;

@RestController
@RequestMapping("/properties")
public class PropertyController {
    private final PropertyService service;
    public PropertyController(PropertyService service) { this.service = service; }

    @PostMapping
    public ResponseEntity<Property> add(@RequestBody @NonNull Property p){
        return ResponseEntity.ok(service.add(p));
    }

    @GetMapping
    public ResponseEntity<List<Property>> all(){ return ResponseEntity.ok(service.allApproved()); }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Property>> allForAdmin(){ return ResponseEntity.ok(service.allForAdmin()); }

    @GetMapping("/admin/pending")
    public ResponseEntity<List<Property>> pendingForAdmin(){ return ResponseEntity.ok(service.pendingForAdmin()); }

    @GetMapping("/available")
    public ResponseEntity<List<Property>> available(){ return ResponseEntity.ok(service.available()); }

    @GetMapping("/{id}")
    public ResponseEntity<Property> getById(@PathVariable @NonNull Long id){
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<Property> approve(@PathVariable @NonNull Long id){
        return ResponseEntity.ok(service.approve(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<Property> reject(@PathVariable @NonNull Long id){
        return ResponseEntity.ok(service.reject(id));
    }
}

