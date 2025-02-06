package com.example.eventifyeventmanagment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(description="Details about book event ticket request")
public class BookEventTicketRequestDTO {
    @Schema(example = "1")
    private Integer userId;
    @Schema(description = "user can book no  of seats  can book", required= true,example = "3")
  //  @NotNull(message = "no of seats  is required")
    private Integer noOfSeats;
}
