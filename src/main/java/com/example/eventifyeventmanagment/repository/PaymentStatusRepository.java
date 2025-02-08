package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.entity.PaymentStatus;
import com.example.eventifyeventmanagment.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface  PaymentStatusRepository  extends JpaRepository<PaymentStatus,Integer> {


}
