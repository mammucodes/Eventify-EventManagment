package com.example.eventifyeventmanagment.dto.response;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
public class CreateEventResponse {
    private Long id;
    private  String name;
    private String performer;
    private String category;
    private  String city;
    private Timestamp eventstarttime;
    private Timestamp eventendtime;
    private String description;
    private int organizerId;
    private Integer statusId;
  //  private String status;
    private EventTicketResponse eventTicketResponse;


}
