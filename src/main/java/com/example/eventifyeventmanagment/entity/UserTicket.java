package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_booked_tickets_details")//todo change table name
public class UserTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "tickets_booked", nullable = false) // todo change table name in DB
    private Integer seatsBooked;
    @Column(name = "count")
    private Integer checkInCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    private LocalDateTime ticketBookedTime; // todo in generate localtime when saving in ticketrepository and add column in table


}


