package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification,Integer> {

    Optional<EmailVerification> findByEmail(String email);
}
