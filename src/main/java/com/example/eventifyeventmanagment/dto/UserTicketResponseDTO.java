package com.example.eventifyeventmanagment.dto;

import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@ToString
public class UserTicketResponseDTO {

    private Integer ticketId;
    private Integer seatsBooked;
    private Integer checkInCount;
    private Event event;
    private User user;
    private LocalDateTime ticketBookedTime;
}
