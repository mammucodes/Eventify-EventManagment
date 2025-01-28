package com.example.eventifyeventmanagment.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@Entity
@Table(name = "email_otp_verification")

public class EmailVerification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String otp;
    @Column(name ="otp_created_on",nullable = false)
    private LocalDateTime otpCreatedOn;

    public EmailVerification(String email, String otp) {
        this.email = email;
        this.otp = otp;
    }

    public EmailVerification() {

    }
}
