package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event,Long> {

    List<Event> findByPerformerAndEventStartTime(String xyz, Timestamp event_start_time);

    List<Event> findByCity(String city);

    Event findByIdAndOrganizerId(int id, int userId);

    List<Event> findByCityAndNameContaining(String city, String name);


    List<Event> findByCityAndPerformerContaining(String city, String performer);

    // List<Event> findByCityAndEventStartTimeContaining(String city, Timestamp eventStartTime);


//    @Query("SELECT e FROM Event e WHERE e.city = :city " +
//            "AND (:performer IS NULL OR e.performer LIKE CONCAT('%', :performer, '%')) " +
//            "AND (:name IS NULL OR e.name LIKE CONCAT('%', :name, '%'))")
//    List<Event> findByCityAndFilters(@Param("city") String city,
//                                     @Param("performer") String performer,
//                                     @Param("name") String name);





    @Query("SELECT e FROM Event e WHERE e.city = :city " +
            "AND e.performer LIKE :performer " +
            "AND e.name LIKE :name "+
            "AND e.statusId = :statusId "+
            "AND e.eventStartTime >= CURRENT_TIMESTAMP")
    List<Event> findByCityAndFilters(@Param("city") String city,
                                     @Param("performer") String performer,
                                     @Param("name") String name,
                                     @Param("statusId") Integer statusId);


    @Query("SELECT e FROM Event e WHERE e.city = :city " +
            "AND e.performer LIKE :performer " +
            "AND e.name LIKE :name " +
            "AND e.statusId = :statusId "+
            "AND e.eventStartTime BETWEEN :startDate AND :endDate"
            )
    List<Event> findByCityAndFilters(@Param("city") String city,
                                     @Param("performer") String performer,
                                     @Param("name") String name,
                                     @Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     @Param("statusId") Integer statusId);



    @Query("SELECT e FROM Event  e WHERE e.performer =:performer "+
            "AND ((:newStartTime BETWEEN e.eventStartTime AND e.eventEndTime) " +
            "OR (:newEndTime BETWEEN   e.eventStartTime AND e.eventEndTime))")
    List<Event> findByPerformerAndFilters(@Param("performer") String performer,
                                          @Param("newStartTime") Timestamp newStartTime,
                                          @Param("newEndTime") Timestamp newEndTime);




}



