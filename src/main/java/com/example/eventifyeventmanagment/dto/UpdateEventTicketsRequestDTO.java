package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class UpdateEventTicketsRequestDTO {

private  Integer organizerId;
private Integer newTicketsAdded;
private Integer newTicketPrice ;
private Integer maxTicketsPerUser;
}
