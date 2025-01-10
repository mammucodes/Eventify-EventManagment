package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    // Find user by username
   // Optional<User> findByName(String name);

    // Find user by email
    Optional<User> findByEmail(String email);
}

