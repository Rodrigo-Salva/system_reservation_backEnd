package com.rodrigo.curso.springboot.app.system_reservation_back.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hall")
@Data
public class Hall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Double pricePerHour;

    private String location;

    @Column(nullable = false)
    private Boolean active = true;
}
