package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name="event_status_options")
@Data

public class Status {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;
    @Column(name="status",length=50 ,nullable = false, unique = true)
    private String status;
}
