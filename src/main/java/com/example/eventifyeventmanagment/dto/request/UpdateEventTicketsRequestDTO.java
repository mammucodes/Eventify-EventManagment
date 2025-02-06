package com.example.eventifyeventmanagment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(name = "details about updateEventTickets Details")
public class UpdateEventTicketsRequestDTO {
@Schema(description = "organizer Id",example="4")
private  Integer organizerId;
@Schema(description = "no of tickets  to be added to event",example = "50")
private Integer newTicketsAdded;
@Schema(description = "ticket price for the event",example="30$")
private Integer newTicketPrice ;
@Schema(description = "max seats a user can book for the event",example = "3")
private Integer maxTicketsPerUser;
}
