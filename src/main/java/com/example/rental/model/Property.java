package com.example.rental.model;

import jakarta.persistence.*;

@Entity
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String location;
    private Double price;
    private boolean available;

    @Enumerated(EnumType.STRING)
    private PropertyType type = PropertyType.RENT;

    @Enumerated(EnumType.STRING)
    private PropertyStatus status = PropertyStatus.AVAILABLE;

    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.PENDING;

    private boolean isScouted = false;

    public enum PropertyType { RENT, SALE }
    public enum PropertyStatus { AVAILABLE, BOOKED, MAINTENANCE }
    public enum ApprovalStatus { PENDING, APPROVED, REJECTED }

    // constructors
    public Property() {}
    public Property(String name, String location, Double price) {
        this.name = name;
        this.location = location;
        this.price = price;
    }

    // getters & setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public PropertyType getType() { return type; }
    public void setType(PropertyType type) { this.type = type; }

    public PropertyStatus getStatus() { return status; }
    public void setStatus(PropertyStatus status) { this.status = status; }

    public ApprovalStatus getApprovalStatus() { return approvalStatus; }
    public void setApprovalStatus(ApprovalStatus approvalStatus) { this.approvalStatus = approvalStatus; }

    public boolean isScouted() { return isScouted; }
    public void setScouted(boolean scouted) { isScouted = scouted; }
}
