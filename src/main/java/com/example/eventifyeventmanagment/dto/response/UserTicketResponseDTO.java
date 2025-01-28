package com.example.eventifyeventmanagment.dto.response;

import com.example.eventifyeventmanagment.dto.response.EventDetailsResponse;
import com.example.eventifyeventmanagment.dto.response.UserDetailsResponse;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@ToString
public class UserTicketResponseDTO {

    private Integer ticketId;
    private Integer seatsBooked;
    private Integer checkInCount;
    private EventDetailsResponse event;
    private UserDetailsResponse user;
    private LocalDateTime ticketBookedTime;
}
