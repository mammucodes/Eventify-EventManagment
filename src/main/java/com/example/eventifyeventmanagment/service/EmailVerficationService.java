package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.EmailNotVerifedException;
import com.example.eventifyeventmanagment.entity.EmailVerification;
import com.example.eventifyeventmanagment.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class EmailVerficationService {

    private EmailVerificationRepository emailVerificationRepository;
    @Autowired
    public EmailVerficationService(EmailVerificationRepository emailVerificationRepository){
        this.emailVerificationRepository = emailVerificationRepository;
    }

    public  void  saveEmailOtpInTable(String email, String otp) throws EmailNotVerifedException {
        EmailVerification emailVerification = new EmailVerification(email, otp);

    EmailVerification emailOtpStored =   emailVerificationRepository.save(emailVerification);
    if(emailOtpStored==null){
          throw new EmailNotVerifedException("uable to save data in db");
    }


    }
}
