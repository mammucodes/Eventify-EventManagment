package com.example.eventifyeventmanagment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;

@Data
@ToString
@Schema(description = "Filters to query events")
public class GetEventDetailsFiltersByDto {

    @Schema(description = "name of the event", example = "Tollwoord nights")
    private  String name;
    private String performer;
    private String category;
    private  String city;
    private LocalDate eventStartDate;
    private String status;


}
