package com.example.eventifyeventmanagment.dto.response;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventTicketResponse {
    private int id;
    private int  availableTickets;
    private int ticketPrice;
    private int maxTicektsCanBook;

}
