package com.example.eventifyeventmanagment.repository;


import com.example.eventifyeventmanagment.entity.EventTicketsDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface EventTicketRepository extends JpaRepository<EventTicketsDetails,Integer> {


    Optional<EventTicketsDetails> findByEventId(Integer eventId);


}
