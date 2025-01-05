package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.dto.*;
//import com.example.eventifyeventmanagment.dto.EventDTO;
//import com.example.eventifyeventmanagment.dto.EventResponse;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("api/events")
public class EventController {
    @Autowired
    private EventService eventservice;
    Logger logger = LoggerFactory.getLogger(EventController.class);

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequest eventrequest) {

        if (eventrequest.getName() == null || eventrequest.getName().trim().isEmpty()) {
            logger.info("Empty or null event name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("Event name cannot be null or empty", "400"));
        }
        if (eventrequest.getName().length() < 2 || eventrequest.getName().length() > 100) {
            logger.info("Validation failed:name charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("name length should be between 2 and 100", "400"));
        }
        if (eventrequest.getPerformer() == null || eventrequest.getPerformer().trim().isEmpty()) {
            logger.info("Empty or null performer name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("performer name cannot be null or empty", "400"));
        }
        if (eventrequest.getPerformer().length() < 2 || eventrequest.getPerformer().length() > 100) {
            logger.info("Validation failed:performer  charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("performer length should be between 2 and 100", "400"));
        }
        if (eventrequest.getCity() == null || eventrequest.getCity().trim().isEmpty()) {
            logger.info("Empty or null city name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("city name cannot be null or empty", "400"));
        }
        if (eventrequest.getCity().length() < 2 || eventrequest.getCity().length() > 100) {
            logger.info("Validation failed:city   charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("city name length should be between 2 and 100", "400"));
        }
        if (eventrequest.getCategory() == null || eventrequest.getCategory().trim().isEmpty()) {
            logger.info("Empty or null cateogry name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("performer name cannot be null or empty", "400"));
        }
        if (eventrequest.getCategory().length() < 2 || eventrequest.getCategory().length() > 100) {
            logger.info("Validation failed:category  charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("category length should be between 2 and 100", "400"));
        }
        if (eventrequest.getEventStartTime() == null) {
            logger.info("null start time  is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("eventstarttime  cannot be null ", "400"));
        }
        if (eventrequest.getEventEndTIme() == null) {
            logger.info("null end time  is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("eventendtime  cannot be null", "400"));
        }
        // Proceed with eventcreation  if all validations pass
        Event event = eventservice.createEvent(eventrequest);
        CreateEventResponse response = new CreateEventResponse();
//response.setId(event.getId);
//response.setName(event.getName);
        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/{city}")
    public ResponseEntity<List<EventDetailsResponse>> getEventDetails(
            @PathVariable String city,
            @RequestParam String category,
            @RequestParam String performer,
            @RequestParam Date eventstartdate) {

        List<Event> events = eventservice.getEventDetails(city, category, performer, eventstartdate);
        for (Event event : events) {
            EventDetailsResponse response = new EventDetailsResponse();
//            response.setName(event.getName);
//            response.setPerformer(event.getPerformer);
//            response.setCity(event.getCity);
//            response.setEventStartTime(event.getEventStartTime);
//            response.setEventEndTIme(event.getEvetnEndTime);
//            response.setCategory(event.getCategory);
//        }
        return null;
    }


//    @PostMapping("/update/{id}")
//    public ResponseEntity<?> updateEventDetails(@PathVariable int id,
//                                                @RequestParam String city,
//                                                @RequestParam String category,
//                                                @RequestParam String performer,
//                                                @RequestParam Date eventstartdate) {
//        Event event = eventservice.updateEventDetails(id, city, category, performer, eventstartdate);
//        UpdateEventRepsonse response = new UpdateEventRepsonse();
//      //  response.setEventname(event.getName);
//      //  response.setId(event.getId);
//        response.setMessage("Updated event succesfully");


        return null;
    }


}





