package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String name;

    @Column(nullable = false, length = 250)
    private String password;

    @Column(nullable = false, length = 250)
    private String email;

    @Column(nullable = false)
    private boolean isorganizer;

    @Column(name = "registered_on", nullable = false)
    private LocalDateTime registeredOn;  // Date and time when the user registered
}
