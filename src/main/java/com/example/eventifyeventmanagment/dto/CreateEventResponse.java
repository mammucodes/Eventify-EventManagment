package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class CreateEventResponse {
    private String message;
    private int id;
    private String name;


}
