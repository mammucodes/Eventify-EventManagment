package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.DuplicateEmailException;
import com.example.eventifyeventmanagment.Exceptions.EmailNotVerifedException;
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


    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;


    private EmailVerificationRepository emailVerificationRepository;


    private UserRepository userRepository;

    private UserService userService;

    private EmailVerficationService emailVerficationService;

    Logger logger = LoggerFactory.getLogger(EmailService.class);
@Autowired
    public EmailService(JavaMailSender  mailSender,
                       UserRepository userRepository,
                        UserService userService,
                        EmailVerficationService emailVerficationService,
                        EmailVerificationRepository emailVerificationRepository) {

        this.mailSender = mailSender;
        this.userRepository = userRepository;
        this.userService = userService;
        this.emailVerficationService = emailVerficationService;
        this.emailVerificationRepository = emailVerificationRepository;
    }

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

    public String generateOTP() {
        return String.valueOf((int) (Math.random() * 900000) + 100000); // 6-digit random OTP
    }

    public void sendOtpEmail(String email) throws EmailNotVerifedException {

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

    public User verifyOtpAndRegisterUser(VerifyOTPandRegisterUserDTO verifyOtp) throws EmailNotVerifedException, DuplicateEmailException {

        Optional<User> existingUserByEmail = userRepository.findByEmail(verifyOtp.getEmail());
        if (existingUserByEmail.isPresent()) {
            logger.error("Registration failed: Email {} already exists", verifyOtp.getEmail());
            throw new DuplicateEmailException("Email already presernt" + verifyOtp.getEmail());

        }
        String otp = verifyOtp.getOtp();
        String email = verifyOtp.getEmail();
        String name = verifyOtp.getName();
        String password = verifyOtp.getPassword();
        Optional<EmailVerification> record = emailVerificationRepository.findByEmail(email);


        if (record.isPresent()) {
            EmailVerification emailVerifed = record.get();
            if (emailVerifed.getOtp().equals(otp)) {
                emailVerificationRepository.save(record.get());

            } else {
                throw new EmailNotVerifedException("invalid otp /or failed to validate email");
            }
            UserRegistrationDTO registrationDTO = new UserRegistrationDTO(name, email, password);
            User savedUser = userService.registerUser(registrationDTO);

            logger.info("User sucessfully registered");
            return savedUser;
            // return ResponseEntity.ok("User successfully registered!");
        } else {
            throw new EmailNotVerifedException("invalid otp /or failed to validate email");
        }
    }
}

