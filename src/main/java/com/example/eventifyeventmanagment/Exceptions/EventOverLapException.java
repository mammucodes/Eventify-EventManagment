package com.example.eventifyeventmanagment.Exceptions;

import lombok.Data;

@Data
public class EventOverLapException  extends Exception{
    private String code;
    public  EventOverLapException(String message,String code){
         super(message);
         this.code = code;
    }
}
