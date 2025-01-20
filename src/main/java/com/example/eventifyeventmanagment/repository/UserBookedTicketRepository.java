package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.entity.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserBookedTicketRepository extends JpaRepository<UserTicket, Integer> {

//    @Query("Select t from UserTicket t  " +
//            "join Event e on e.id = t.event " +
//            "join User u on u.id=t.userId " +
//            "where t.id =:ticketId")
//    Optional<UserTicket> findByIdandFilters(Integer ticketId);

}
