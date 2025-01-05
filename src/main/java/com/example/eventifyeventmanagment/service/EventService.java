package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.dto.CreateEventRequest;

import com.example.eventifyeventmanagment.entity.Event;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class EventService {
    public Event createEvent(CreateEventRequest request) {
return null;
    }

    public List<Event> getEventDetails(String city, String category, String performer, Date eventstartdate) {
        List<Event> events = new ArrayList<>();
         return events;
    }

    public Event updateEventDetails(int id, String city, String category, String performer, Date eventstartdate) {
        return null;
    }
}
