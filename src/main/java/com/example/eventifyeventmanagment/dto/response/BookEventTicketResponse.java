package com.example.eventifyeventmanagment.dto.response;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
public class BookEventTicketResponse {
    private String message;
    private Integer ticketId;
    private LocalDateTime bookedOn;
    private String paymentIntentId;
    private  String paymentStatus;
}
