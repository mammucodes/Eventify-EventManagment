package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.dto.request.UserTicketDTO;
import com.example.eventifyeventmanagment.entity.UserTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserBookedTicketRepository extends JpaRepository<UserTicket, Integer> {


    @Query("select ut from  UserTicket  ut " +
            "join Event e on e.id=ut.event.id " +
            "join User u on u.id = ut.user.id " +
            "where e.eventStartTime between  :nextDayStart and :nextDayEnd " +
            " and e.statusId = :statusId and ut.remainderSent=false ")

    List<UserTicket> findUsersToSendTodaysEventRemainders(@Param("nextDayStart") LocalDateTime nextDayStart,
                                                          @Param("nextDayEnd") LocalDateTime nextDayEnd,
                                                              @Param("statusId") Integer statusId);

//todo   try to get only required feilds instead of complete  columns from all 3 tables


    @Query("SELECT new com.example.eventifyeventmanagment.dto.request.UserTicketDTO" +
            "(e.name, e.performer, e.eventStartTime,e.city, ut ,u.email,u.id) " +
            "FROM UserTicket ut " +
            "JOIN ut.event e " +
            "JOIN ut.user u " +
            "WHERE e.eventStartTime BETWEEN :nextDayStart AND :nextDayEnd " +
            "AND e.statusId = :statusId " +
            "AND ut.remainderSent = false")
    List<UserTicketDTO> findUsersToSendNextDayEventRemainders(
            @Param("nextDayStart") LocalDateTime nextDayStart,
            @Param("nextDayEnd") LocalDateTime nextDayEnd,
            @Param("statusId") Integer statusId
    );




    @Query("SELECT ut FROM UserTicket ut WHERE ut.event.id = :eventId")
    List<UserTicket> findTicketsByEventId(@Param("eventId") Integer eventId);

//    @Query("Select t from UserTicket t  " +
//            "join Event e on e.id = t.event " +
//            "join User u on u.id=t.userId " +
//            "where t.id =:ticketId")
//    Optional<UserTicket> findByIdandFilters(Integer ticketId);


}
