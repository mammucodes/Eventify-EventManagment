package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookEventTicketRequestDTO {
    private Integer userId;
    private Integer noOfSeats;
}
