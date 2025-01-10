package com.example.eventifyeventmanagment.dto;

import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Data
@ToString
public class GetEventDetailsFiltersByDto {
    private  String name;
    private String performer;
    private String category;
    private  String city;
    private LocalDate eventStartDate;

}
