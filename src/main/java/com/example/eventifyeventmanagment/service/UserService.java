package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
import com.example.eventifyeventmanagment.Exceptions.UserNotFoundException;
import com.example.eventifyeventmanagment.dto.request.UserRegistrationDTO;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;

//    @Autowired
//    private EmailService emailService;
    //this created circular dependency and result in start application

    @Autowired // constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;

    }

    public UserService() {

    }

    //This method takes userrequest  object and
    // if user is not  already present in database it saves the user
    // or else if already exist it throws Duplication EmailException
    public User registerUser(UserRegistrationDTO registrationDto) throws DuplicateEmailException {
        logger.info("Attempting to register a new user with username: {}", registrationDto.getName());


// we are checking only if email exist or not user name can be same


        // Create a new user and set the fields
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(registrationDto.getPassword()); //  need to Encrypt the password
        user.setRegisteredOn(LocalDateTime.now()); // Set the current registration date and time
        user.setIsorganizer(registrationDto.isOrganizer());
        // Save the user to the database
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return savedUser;
    }





    // This method takes user id and checks if present  returns user object
    // if not it throws user not found excepton
    public User getUserDetails(int id) throws UserNotFoundException {
        logger.info("attempting to get user details by using user id");
        Optional<User> optionaluser = userRepository.findById((long) id);
        if (optionaluser.isPresent()) {
            User user = optionaluser.get();
            return user;
            // Proceed with user object
        } else {
            // Handle case where user is not found
            throw new UserNotFoundException("User not found with id: " + id);
        }

    }
}


