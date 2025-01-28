package com.example.eventifyeventmanagment.dto.request;

import jdk.jfr.DataAmount;
import lombok.*;


@ToString
@Getter
@Setter
public class UserRegistrationDTO {
    private String name;
    private  String email;
    private String password;
    private  boolean isOrganizer;

    public UserRegistrationDTO(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

//    public UserRegistrationDTO(String abc, String xyz) {
//        this.name = abc;
//        this.email = xyz;
//    }

    // if you are setting values from postman or any other api you need default   constructor a
    //amd setters  methods value for sure
    //if you are sending a resposne to postmon as json you need getter methods













    @Override
    public String toString() {
        return "UserRegistrationDTO{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isOrganizer=" + isOrganizer +
                '}';
    }
}
