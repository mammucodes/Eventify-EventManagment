package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.EventNotFoundException;
import com.example.eventifyeventmanagment.dto.CreateEventRequestDTO;

import com.example.eventifyeventmanagment.dto.CreateEventRequestDTO;
import com.example.eventifyeventmanagment.dto.GetEventDetailsFiltersByDto;
import com.example.eventifyeventmanagment.dto.UpdateEventRequestDTO;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.repository.EventRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {
    Logger logger = LoggerFactory.getLogger(EventService.class);
    @Autowired
    private EventRepository eventrepository;
//THis method takes eventrequest object and   save it to the event database
    //and returns the event response
    public Event createEvent(CreateEventRequestDTO requestdto) {
        logger.info("Creating an event requestdto called");


        Event event = new Event();
        event.setName(requestdto.getName());
        event.setPerformer(requestdto.getPerformer());
        event.setCity(requestdto.getCity());
        event.setCategory(requestdto.getCategory());
        event.setEventStartTime(requestdto.getEventstarttime());
        event.setEventEndTIme(requestdto.getEventendtime());
        event.setDescription(requestdto.getDescription());

        Event savedEvent = eventrepository.save(event);
        logger.info("successsfully created and saved an event with event id" + savedEvent.getId());
        return savedEvent;


    }
//THis method takes city name as input and  returns all the event present in that city
    public List<Event> getEventDetails(String city) {
        logger.info("Trying to get event details by city name");
        List<Event> events = eventrepository.findByCity(city);
        if (events == null) {
            logger.info("no events presetn in the city");
        }
        logger.info("sucessfully got evet details");
        return events;
    }

    public List<Event> getEventDetails(String city, GetEventDetailsFiltersByDto geteventsrequest) {
        List<Event> events = null;
        if (geteventsrequest == null) {
            logger.info("get events details by just city name");
            events = eventrepository.findByCity(city);
            return events;
        }

        String eventName = geteventsrequest.getName();
        String performer = geteventsrequest.getPerformer();
        LocalDate eventStartDate = geteventsrequest.getEventStartDate();
        String performerParam = (performer == null) ? "%" : "%" + performer + "%";
        String nameParam = (eventName == null) ? "%" : "%" + eventName + "%";
        if (eventStartDate == null) {
            events = eventrepository.findByCityAndFilters(city, performerParam, nameParam);
            return events;
        }
        // Calculate start and end of the given date
        LocalDateTime startDate = eventStartDate.atStartOfDay(); // 12:00 a.m.
        LocalDateTime endDate = eventStartDate.atTime(23, 59, 59); // 11:59 p.m.

        events = eventrepository.findByCityAndFilters(city, performerParam, nameParam, startDate, endDate);
        return events;
    }

    public Event updateEventDetails(int id, UpdateEventRequestDTO updateeventrequestdto) throws EventNotFoundException {

        // Fetch the existing event by ID
        Optional<Event> optionalEvent = eventrepository.findById((long) id);

        if (optionalEvent.isPresent()) {
            Event event = optionalEvent.get();

            // Update the fields
            if (updateeventrequestdto.getName() != null) {
                event.setName(updateeventrequestdto.getName());
            }
            if (updateeventrequestdto.getCity() != null) {
                event.setCity(updateeventrequestdto.getCity());
            }
            if (updateeventrequestdto.getCategory() != null) {
                event.setCategory(updateeventrequestdto.getCategory());
            }
            if (updateeventrequestdto.getPerformer() != null) {
                event.setPerformer((updateeventrequestdto.getPerformer()));
            }
            if (updateeventrequestdto.getEventstarttime() != null) {
                event.setEventStartTime(updateeventrequestdto.getEventstarttime());
            }
            if (updateeventrequestdto.getEventendtime() != null)
                event.setEventEndTIme(updateeventrequestdto.getEventendtime());
            // Save the updated entity
            return eventrepository.save(event);
        } else {
            throw new EventNotFoundException("Event not found with ID: " + id);
        }
    }


}

