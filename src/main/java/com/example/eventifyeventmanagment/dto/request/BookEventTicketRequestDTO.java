package com.example.eventifyeventmanagment.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class BookEventTicketRequestDTO {
    private Integer userId;
    private Integer noOfSeats;
}
