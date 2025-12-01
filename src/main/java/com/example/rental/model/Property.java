package com.example.rental.model;

import jakarta.persistence.*;

@Entity
public class Property {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String address;
    private Double price;
    private boolean available = true;
    private String owner;

    // constructors
    public Property() {}
    public Property(String title, String address, Double price, String owner) {
        this.title = title; this.address = address; this.price = price; this.owner = owner;
    }

    // getters & setters (generate via IDE)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
}
