package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
import com.example.eventifyeventmanagment.Exceptions.EmailNotVerifedException;
import com.example.eventifyeventmanagment.Exceptions.OTPExpiredException;
import com.example.eventifyeventmanagment.Exceptions.UserNotFoundException;
import com.example.eventifyeventmanagment.dto.request.EmailDtoRequest;
import com.example.eventifyeventmanagment.dto.request.VerifyOTPandRegisterUserDTO;
import com.example.eventifyeventmanagment.dto.response.ErrorResponse;
import com.example.eventifyeventmanagment.dto.response.UserDetailsResponse;
import com.example.eventifyeventmanagment.dto.request.UserRegistrationDTO;
import com.example.eventifyeventmanagment.entity.EmailVerification;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.service.EmailService;
import com.example.eventifyeventmanagment.service.EmailVerficationService;
import com.example.eventifyeventmanagment.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;

@RestController
@RequestMapping("api/users")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userservice;
    private EmailService emailService;

    private EmailVerficationService emailVerficationService;

    @Autowired
    public UserController(UserService userservice,
                          EmailVerficationService emailVerficationService,
                            EmailService  emailService) {

        this.userservice = userservice;
        this.emailVerficationService = emailVerficationService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDTO userregister) {

        if (userregister.getName() == null || userregister.getName().trim().isEmpty()) {

            ErrorResponse er = new ErrorResponse();
            er.setMessage("Name should not be null or empty");
            er.setErrorCode("404");
            return ResponseEntity.badRequest().body(er);
        }
        if (userregister.getName().length() < 3 || userregister.getName().length() > 100) {
            logger.error("Validation failed: Username length is invalid");
            return ResponseEntity.badRequest().body(new ErrorResponse("Username must be between 3 and 20 characters", "400"));
        }

        if (userregister.getEmail() == null || userregister.getEmail().trim().isEmpty()) {
            logger.error("Validation failed: Email is missing or empty email passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("Email must not be empty", "400"));
        }

        if (!isValid(userregister.getEmail())) {
            logger.error("Validation failed:Invalid email passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("must pass Valid email", "400"));

        }
        if (userregister.getPassword() == null || userregister.getPassword().trim().isEmpty()) {
            logger.error("Validation failed: null or empty password passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("Cannot pass an empty or null passwprd", "400"));
        }

        // Check if the password meets the regex criteria
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{6,}$";
        if (!userregister.getPassword().matches(passwordRegex)) {
            logger.error("Validation failed: Password does not meet passwordRegex  requirements");
            return ResponseEntity.badRequest().body(
                    new ErrorResponse(
                            "Password must have at least 6 characters, including one uppercase letter, one lowercase letter, and one number.",
                            "400"));
        }

        // Proceed with user registration if all validations pass
        User registeredUser;
        try {
            registeredUser = userservice.registerUser(userregister);
        } catch (DuplicateEmailException dex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("This email already  exist try to give new email", "400"));
        }

        logger.info("User registered successfully with ID: {}", registeredUser.getId());
        return ResponseEntity.ok("User registered successfully with ID: " + registeredUser.getId());
        //return ResponseEntity.ok().body("success");
    }
//another way to do it
//    @PostMapping("/register")
//    public ResponseEntity<?> register(@RequestParam String email) {
//        return null;
//    }
//
//    @PostMapping("/verify/useremail")
//    public ResponseEntity<?> verify(@RequestParam String email) {
//        return null;
//    }
    @PostMapping("/email-validate")
    public ResponseEntity<?> sendOtp(@RequestParam String email) throws EmailNotVerifedException {
        if (email == null || email.trim().isEmpty()) {
            logger.error("Validation failed: Email is missing or empty email passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("Email must not be empty", "400"));
        }

        if (!isValid(email) ){
            logger.error("Validation failed:Invalid email passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("must pass Valid email", "400"));

        }

            emailService.sendOtpEmail(email);
            return ResponseEntity.ok(" please verify your email OTP sent to " + email);
    }
    @PostMapping("/register-verified-email")
    public ResponseEntity<?> verifyOtpAndRegisterUser(@RequestBody VerifyOTPandRegisterUserDTO verifyOTPandRegisterUserDTO) throws EmailNotVerifedException, DuplicateEmailException, OTPExpiredException {

       User user =  emailVerficationService.verifyOtpAndRegisterUser(verifyOTPandRegisterUserDTO);
        return  ResponseEntity.ok("sucessfully registered the user with Id"+user.getId());

}


    @GetMapping("/{id}")
    public ResponseEntity<UserDetailsResponse> getUserDetails(@PathVariable int id) {

        //todo only proceed to next lines of code if authenticated else throw 401 error
        User user = null;
        try {
            user = userservice.getUserDetails(id);
        } catch (UserNotFoundException se) {
            ResponseEntity.badRequest().body(new ErrorResponse("no such user found with given ID" + id + " ", " 400 "));
        }

        UserDetailsResponse response = new UserDetailsResponse();
        assert user != null;
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setId(Math.toIntExact(user.getId()));
        response.setOrganizer(user.isIsorganizer());
        response.setRegister_on(user.getRegisteredOn());
        LocalDate currentDate = LocalDate.now();

        // Calculate the difference
        Period period = Period.between(LocalDate.from(user.getRegisteredOn()), currentDate);
        int AccountdaysActive = period.getYears() * 365 + period.getMonths() * 30 + period.getDays(); // Approximate calculation


        response.setAccountDays((long) AccountdaysActive);
        return ResponseEntity.ok(response);


    }

    @PostMapping("/remainder/email")
    public ResponseEntity<?> sendEmailToUsers(@RequestBody EmailDtoRequest emailrequestdto) {
        String email = emailrequestdto.getEmailId();
        String subject = emailrequestdto.getSubject();
        String body = emailrequestdto.getBody();
        try {
            emailService.sendEmail(email, subject, body);
            return ResponseEntity.ok("email sent sucessfully ");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Email  sending failed");
        }

    }

    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return email.matches(emailRegex);

    }
}
