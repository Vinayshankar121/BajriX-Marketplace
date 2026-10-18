package com.bajrix.marketplace.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "sellers")
public class Seller {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SellerStatus status;

    public Seller() {
    }

    public Seller(String name, SellerStatus status) {
        this.name = name;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public SellerStatus getStatus() { return status; }
    public void setStatus(SellerStatus status) { this.status = status; }
}
