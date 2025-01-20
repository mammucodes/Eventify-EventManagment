package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
@Data
@ToString
public class EventDetailsResponse {
    private  String name;
    private String performer;
    private String category;
    private  String city;
    private Timestamp eventStartTime;
    private Timestamp eventEndTIme;
    private String Description;
    private Integer organizerId;

    private String  status;

}
