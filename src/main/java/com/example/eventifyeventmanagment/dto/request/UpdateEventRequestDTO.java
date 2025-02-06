package com.example.eventifyeventmanagment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
@Data
@ToString
@Schema(description = "Details about an update event request object")
public class UpdateEventRequestDTO {
    private  String name;
    private String performer;
    private String category;
    private  String city;
    private Timestamp eventstarttime;
    private Timestamp eventendtime;
    private String description;
}
