package com.example.eventifyeventmanagment.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmailDtoRequest {
    private String emailId;
    private String subject;
    private String body;
}
