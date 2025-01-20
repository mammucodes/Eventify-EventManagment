package com.example.eventifyeventmanagment.repository;

import com.example.eventifyeventmanagment.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StatusRepository extends JpaRepository<Status, Integer> {
// optional<T> is used when  a query returns atmost one object . but  we put status as not null and unique, if it returns
    //null object then optional will help to catch null object and covnert to empty object

    // if you want to get Status object then use status.get() , it will return Status object
    Optional<Status> findByStatus(String status); //select * from status where status="

}

