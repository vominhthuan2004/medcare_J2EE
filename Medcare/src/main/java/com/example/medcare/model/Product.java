package com.example.medcare.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @org.hibernate.annotations.Nationalized
    private String name;

    @org.hibernate.annotations.Nationalized
    @Column(length = 1000)
    private String description;

    private double price;

    private String imageUrl;

    private int stock;
}