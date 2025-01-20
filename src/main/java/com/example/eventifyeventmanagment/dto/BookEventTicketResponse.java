package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookEventTicketResponse {
    private String message;
    private Integer ticketId;
}
