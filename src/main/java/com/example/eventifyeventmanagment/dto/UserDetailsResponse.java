package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString

public class UserDetailsResponse {
    private  int id;
    private String name;
    private String email;
    private boolean isOrganizer;
    private Long accountDays;
    private LocalDateTime register_on;
}
