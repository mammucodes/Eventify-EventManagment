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
    private boolean  isOrganizer;

    //writting getter andsetters for isOrganizer. becuase  by using @Data lombok is giving getter and setter as setOrganizerId() and getOrganizerId() which is missing Is
   // whereas when converting from json  to object(de-serizaliation) request spring jackson is excepting setIsOrganizer . since it is not found it set
    // isOrganizer to default value false efen though it is passed as true

    // if you   dont want to create getter and setters then use Boolean object type . and if no value is passed set organizer to false if null is passed
    public boolean getIsOrganizer() {
        return isOrganizer;
    }

    public void setIsOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }
}
