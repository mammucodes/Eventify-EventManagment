package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
import com.example.eventifyeventmanagment.Exceptions.DuplicateUsernameException;
import com.example.eventifyeventmanagment.Exceptions.UserNotFoundException;
import com.example.eventifyeventmanagment.dto.ErrorResponse;
import com.example.eventifyeventmanagment.dto.UserDetailsResponse;
import com.example.eventifyeventmanagment.dto.UserRegistrationDTO;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;

@RestController
@RequestMapping("api/users")
public class UserController {
    Logger logger = LoggerFactory.getLogger(UserController.class);

    private UserService userservice;
    @Autowired
    public  UserController(UserService userservice){
        this.userservice = userservice;
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


    private boolean isValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@[a-zA-Z0-9-]+(?:\\.[a-zA-Z0-9-]+)*$";
        return email.matches(emailRegex);

    }
}
