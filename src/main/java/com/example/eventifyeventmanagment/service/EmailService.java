package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
import com.example.eventifyeventmanagment.Exceptions.EmailNotVerifedException;
import com.example.eventifyeventmanagment.Exceptions.OTPExpiredException;
import com.example.eventifyeventmanagment.dto.request.UserRegistrationDTO;
import com.example.eventifyeventmanagment.dto.request.VerifyOTPandRegisterUserDTO;
import com.example.eventifyeventmanagment.dto.response.UserDetailsResponse;
import com.example.eventifyeventmanagment.entity.EmailVerification;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.repository.EmailVerificationRepository;
import com.example.eventifyeventmanagment.repository.UserRepository;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class EmailService {




    @Value("${spring.mail.username}")
    private String emailFrom;

    private JavaMailSender mailSender;
    private EmailVerificationRepository emailVerificationRepository;
    private EmailVerficationService emailVerficationService;

    Logger logger = LoggerFactory.getLogger(EmailService.class);
@Autowired
    public EmailService(JavaMailSender  mailSender,
                        EmailVerficationService emailVerficationService,
                        EmailVerificationRepository emailVerificationRepository) {

        this.mailSender = mailSender;
        this.emailVerficationService = emailVerficationService;
        this.emailVerificationRepository = emailVerificationRepository;
    }
//this method  helps to send email to given email address
    //if failed to send it throws an exception
    public void sendEmail(String toEmail, String subject, String body) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(new InternetAddress(emailFrom, "Event Reminders"));

            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//this method genrates random 6 digts otp and
     // returns the otp number
    public String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit random OTP
    }
//This method take email as input and sends otp  to valid email address
    //if already email is present in emailverfication db then it will delte old otp value ang generates new otp
    public void sendOtpEmail(String email) throws EmailNotVerifedException {
    Optional< EmailVerification> emailVerification = emailVerificationRepository.findByEmail(email);
logger.info("trying to check if this email already present in DB ");
    if(emailVerification.isPresent()){
        logger.info("passed email is already present in email_otp_verification table before sending another otp deleting the previous otp value with given email");
        EmailVerification emailPresent = emailVerification.get();
        emailVerificationRepository.delete(emailPresent);
    }

        String otp = generateOTP();

        // Save OTP to the database
        emailVerficationService.saveEmailOtpInTable(email, otp);

        logger.info("Saved email and otp in data base");

        logger.info("sending an email ");
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Eventify  app registration  OTP Code");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
        logger.info("sent otp to an email sucessfully");
    }


}

