package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Timestamp;

@Entity
@Table(name = "events")
@Data
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
    @Column(nullable = false,length = 100 )
    private String performer;
    @Column(nullable = false,length =  100)
    private String city;
    @Column(nullable = false,length = 100)
    private String category;
    @Column(name = "event_start_time", nullable = false,length = 100)
    private Timestamp eventStartTime; //eventStartTime
    @Column(name = "event_end_time",nullable = false, length = 100)
    private Timestamp eventEndTime ;
    @Column(length = 100)
    private String description;
    @Column(length = 100)
    private String descrption;
    @Column(name="organizer_id")
    private Integer  organizerId ;
    @Column(name = "status_id",nullable = false)
    private Integer statusId;



}
