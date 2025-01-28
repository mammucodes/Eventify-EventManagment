package com.example.eventifyeventmanagment.dto.response;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
@Data
@ToString
public class ErrorResponse {
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    public ErrorResponse(){

    }
    public ErrorResponse(String message, String errorcode){
        this.message = message;
        this.errorCode = errorcode;
        this.timestamp = LocalDateTime.now();
    }


}
