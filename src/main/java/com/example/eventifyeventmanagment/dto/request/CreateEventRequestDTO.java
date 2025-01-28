package com.example.eventifyeventmanagment.dto.request;


import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class CreateEventRequestDTO {
    private  String name;
    private String performer;
    private String category;
    private  String city;
    private Timestamp eventstarttime;
    private Timestamp eventendtime;
    private String description;
    private int organizerId;
    private String status;
    private TicketsDetailRequestDTO eventTicketDetails;


}
