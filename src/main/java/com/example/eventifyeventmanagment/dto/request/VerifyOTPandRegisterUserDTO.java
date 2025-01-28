package com.example.eventifyeventmanagment.dto.request;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class VerifyOTPandRegisterUserDTO {
    private String name;
    private String email;
    private String password;
    private String otp;
}
