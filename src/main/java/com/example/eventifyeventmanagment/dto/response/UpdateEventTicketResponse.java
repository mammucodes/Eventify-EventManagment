package com.example.eventifyeventmanagment.dto.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateEventTicketResponse {

    private  Long eventId;
    private  String message;
    private Integer  totalTickets;
    private Integer newTicketPrice;
    private Integer maxTicketsPerUser;
    }


