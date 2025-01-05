package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateEventRepsonse {
    private int id;
    private String message;
    private String eventname;

}
