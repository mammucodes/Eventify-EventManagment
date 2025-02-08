package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "user_tickets_details")
public class UserTicket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "seats_booked", nullable = false)
    private Integer seatsBooked;

    @Column(name = "checkIn_count", nullable = false)
    private int checkInCount ;

    @Column(name = "check_out_count", nullable = false)
    private int checkOutCount;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "event_id", referencedColumnName = "id")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @Column(name = "ticket_booked_time", nullable = false)
    private LocalDateTime ticketBookedOn;

 @Column(name="remainder_sent",nullable = false)
    private boolean remainderSent;
@Column(name="payment_intent_id",nullable = false)
    private String paymentIntentId;
@Column(name="payment_status_id",nullable = true)
    private Integer paymentStatusId;

}


