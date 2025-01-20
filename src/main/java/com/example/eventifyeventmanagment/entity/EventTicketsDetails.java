package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "event_tickets")
public class EventTicketsDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name = "available_tickets", nullable = false)
    private Integer availableTickets;
    @Column(name = "ticekt_price", nullable = false)
    private Integer ticketPrice;
    @Column(name = "max_ticekts_allowed_to_book")
    private Integer maxTicektsCanBook;
    @Column(name = "event_id", nullable = false)
    private Long eventId;
}





