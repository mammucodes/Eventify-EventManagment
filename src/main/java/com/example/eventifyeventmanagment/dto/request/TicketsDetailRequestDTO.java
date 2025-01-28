package com.example.eventifyeventmanagment.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TicketsDetailRequestDTO {
    private int  availableTickets;
    private int ticketPrice;
    private int maxTicketsCanBook;



}
