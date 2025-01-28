package com.example.eventifyeventmanagment.dto.request;

import com.example.eventifyeventmanagment.entity.UserTicket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDateTime;
@Data
@ToString
@AllArgsConstructor
public class UserTicketDTO {

    private String eventName;
    private String performer;
    private Timestamp eventStartTime;
    private String city;
    private UserTicket userTicket;
    private String  email;
    private Long userId;

    //since I user @@AllArgsConstructor no need to explicitly construct the constructor
//    public UserTicketDTO(String eventName, String performer, Timestamp eventStartTime, Integer ticketId) {
//        this.eventName = eventName;
//        this.performer = performer;
//        this.eventStartTime = eventStartTime;
//        this.ticketId = ticketId;
//    }

}
