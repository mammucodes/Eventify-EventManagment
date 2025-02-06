package com.example.eventifyeventmanagment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Schema(description = "Details about verifyOtpAndRegisterUserDto object")
public class VerifyOTPandRegisterUserDTO {
    @Schema(description = "name of the user ", example = "Riya")
    private String name;
    @Schema(description = "email of the user",example = "riay@gmail.com")
    private String email;
    private String password;
    @Schema(description = "otp send to user eamil id", example = "342169")
    private String otp;
    @Schema(description = "asking if user who is  registering is and Organizer ", example = "true")
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
