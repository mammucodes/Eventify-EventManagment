package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
import com.example.eventifyeventmanagment.Exceptions.EmailNotVerifedException;
import com.example.eventifyeventmanagment.Exceptions.OTPExpiredException;
import com.example.eventifyeventmanagment.dto.request.UserRegistrationDTO;
import com.example.eventifyeventmanagment.dto.request.VerifyOTPandRegisterUserDTO;
import com.example.eventifyeventmanagment.entity.EmailVerification;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.repository.EmailVerificationRepository;
import com.example.eventifyeventmanagment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EmailVerficationService {

    private EmailVerificationRepository emailVerificationRepository;

    private UserRepository userRepository;
    private UserService userService;
    Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    public EmailVerficationService(EmailVerificationRepository emailVerificationRepository,
                                   UserRepository userRepository,
                                   UserService userService) {

        this.emailVerificationRepository = emailVerificationRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    //THis method takes email and otp as inputs
    //  and saves  in EmailVerfication table in db
    // throws if email is a=not able to save in db it throws  EmailNotVerfiedException if email is not valid
    public void saveEmailOtpInTable(String email, String otp) throws EmailNotVerifedException {
        EmailVerification emailVerification = new EmailVerification(email, otp);
        emailVerification.setOtpCreatedOn(LocalDateTime.now());

        EmailVerification emailOtpStored = emailVerificationRepository.save(emailVerification);
        if (emailOtpStored == null) {
            throw new EmailNotVerifedException("uable to save data in db");
        }


    }

    //this method will take vertify otp object and checks if passed otp is valid or not and verfies otp and register user
    //  returns  saved User in db object  if all validation passes
    //throws Duplicate email,  otpExpired , emailNotVerifed exceptions
    public User verifyOtpAndRegisterUser(VerifyOTPandRegisterUserDTO verifyOtp) throws EmailNotVerifedException, DuplicateEmailException, OTPExpiredException {

        Optional<User> existingUserByEmail = userRepository.findByEmail(verifyOtp.getEmail());
        if (existingUserByEmail.isPresent()) {
            logger.error("Registration failed: Email {} already exists", verifyOtp.getEmail());
            throw new DuplicateEmailException("Email already presernt" + verifyOtp.getEmail());
        }

        String otp = verifyOtp.getOtp();
        String email = verifyOtp.getEmail();
        String name = verifyOtp.getName();
        String password = verifyOtp.getPassword();
        boolean isOrganizer = verifyOtp.getIsOrganizer();
        //Boolean isOrganizer = verifyOtp.getOrganizer();
        //if(isOrganizer==null)
        //  isOrganizer = false;

        Optional<EmailVerification> record = emailVerificationRepository.findByEmail(email);


        if (record.isPresent()) {
            EmailVerification emailVerifed = record.get();
            LocalDateTime otpCreatedOn = emailVerifed.getOtpCreatedOn();
            LocalDateTime localTimeNow = LocalDateTime.now();

            LocalDateTime currentTimeThreeDaysBefore = localTimeNow.minusDays(3);

            if (emailVerifed.getOtp().equals(otp)) {
                if (otpCreatedOn.isAfter(currentTimeThreeDaysBefore)) {

                    UserRegistrationDTO registrationDTO = new UserRegistrationDTO(name, email, password);
                    registrationDTO.setOrganizer(isOrganizer);
                    User savedUser = userService.registerUser(registrationDTO);

                    logger.info("User sucessfully registered");
                    return savedUser;
                } else {
                    throw new OTPExpiredException("passed otp is expired. please  generate new otp to register user");
                }

            } else {
                throw new EmailNotVerifedException("invalid otp /or failed to validate email");
            }

            // return ResponseEntity.ok("User successfully registered!");
        } else {
            throw new EmailNotVerifedException("invalid otp /or failed to validate email");
        }
    }
}
