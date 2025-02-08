package com.example.eventifyeventmanagment.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EventBookingRequest {
    private Integer userId;
    private Integer eventId;
    private Integer noOfSeats;
  //  private Long amount;
   // private String currency;
}
