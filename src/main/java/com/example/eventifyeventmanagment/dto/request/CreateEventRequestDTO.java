package com.example.eventifyeventmanagment.dto.request;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;

@Data
@ToString
@Schema(description = "Details about createEventRequest object")
public class CreateEventRequestDTO {

    @Schema(description = "Name of the event", example = "Tolly-Nights")
    private  String name;
    @Schema(description = "name of the performer",example = "Bhairava")
    private String performer;
    @Schema(description = "name of cateogry like singing, dancing etc., In which category this event comes",example = "singing")
    private String category;
    @Schema(description ="name of the city where event perforemd",example = "Hyderabad")
    private  String city;
    private Timestamp eventstarttime;
    private Timestamp eventendtime;
    private String description;
    @Schema(description  ="organizer id to create the event",example = "1")
    private int organizerId;
    @Schema(description = "status of the event , we have only 3 options available, cancelled, completed while creating" +
            "san event try to give available ",example="available")
    private String status;
    @Schema(description = "organizer can send event tickets details by passing ticket reuest object values")
    private TicketsDetailRequestDTO eventTicketDetails;


}
