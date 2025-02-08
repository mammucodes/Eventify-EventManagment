package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name="payment_status_options")
@Data
public class PaymentStatus {
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private int id;
    @Column(name="payment_status",length=50 ,nullable = false, unique = true)
    private String paymentStatus;
}
