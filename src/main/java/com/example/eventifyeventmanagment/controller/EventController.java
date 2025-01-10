package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.Exceptions.EventNotFoundException;
import com.example.eventifyeventmanagment.dto.*;
//import com.example.eventifyeventmanagment.dto.EventDTO;
//import com.example.eventifyeventmanagment.dto.EventResponse;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.repository.EventRepository;
import com.example.eventifyeventmanagment.service.EventService;
import org.apache.coyote.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/events")
public class EventController {
    @Autowired
    private EventService eventservice;
    Logger logger = LoggerFactory.getLogger(EventController.class);
    @Autowired
    private EventRepository eventrepository;

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequestDTO eventrequestdto) {

        if (eventrequestdto.getName() == null || eventrequestdto.getName().trim().isEmpty()) {
            logger.info("Empty or null event name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("Event name cannot be null or empty", "400"));
        }
        if (eventrequestdto.getName().length() < 2 || eventrequestdto.getName().length() > 100) {
            logger.info("Validation failed:name charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("name length should be between 2 and 100", "400"));
        }
        if (eventrequestdto.getPerformer() == null || eventrequestdto.getPerformer().trim().isEmpty()) {
            logger.info("Empty or null performer name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("performer name cannot be null or empty", "400"));
        }
        if (eventrequestdto.getPerformer().length() < 2 || eventrequestdto.getPerformer().length() > 100) {
            logger.info("Validation failed:performer  charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("performer length should be between 2 and 100", "400"));
        }
        if (eventrequestdto.getCity() == null || eventrequestdto.getCity().trim().isEmpty()) {
            logger.info("Empty or null city name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("city name cannot be null or empty", "400"));
        }
        if (eventrequestdto.getCity().length() < 2 || eventrequestdto.getCity().length() > 100) {
            logger.info("Validation failed:city   charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("city name length should be between 2 and 100", "400"));
        }
        if (eventrequestdto.getCategory() == null || eventrequestdto.getCategory().trim().isEmpty()) {
            logger.info("Empty or null cateogry name is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("performer name cannot be null or empty", "400"));
        }
        if (eventrequestdto.getCategory().length() < 2 || eventrequestdto.getCategory().length() > 100) {
            logger.info("Validation failed:category  charcters are less than 2 or  more  than 100  passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("category length should be between 2 and 100", "400"));
        }
        if (eventrequestdto.getEventstarttime() == null) {
            logger.info("null start time  is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("eventstarttime  cannot be null ", "400"));
        }
        if (eventrequestdto.getEventendtime() == null) {
            logger.info("null end time  is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("eventendtime  cannot be null", "400"));
        }
        List<Event> events = eventrepository.findByPerformerAndEventStartTime(eventrequestdto.getPerformer(), eventrequestdto.getEventstarttime());

        if (!events.isEmpty()) {
            logger.info("THis eperformer is already doing an event ");
            return ResponseEntity.badRequest().body(new ErrorResponse("There is already event present woith same perfrmer at same time cannot create another event", "400"));

        }
        // Proceed with eventcreation  if all validations pass
        Event event = eventservice.createEvent(eventrequestdto);
        CreateEventResponse responsedto = new CreateEventResponse();

        responsedto.setId(Math.toIntExact(event.getId()));
        responsedto.setName(event.getName());
        responsedto.setMessage("succesfully created an event with event id" + responsedto.getId());
        return ResponseEntity.ok().body(responsedto);

    }


    @GetMapping("/{city}")
    public ResponseEntity<?> getEventDetails(
            @PathVariable String city,
            @RequestBody(required = false) GetEventDetailsFiltersByDto geteventrequest
           ) {
        List<Event> events;

        if (city == null || city.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("City name cannot be null or empty", "400"));
        }
        events = eventservice.getEventDetails(city, geteventrequest);
        if(events==null||events.isEmpty()){
            return ResponseEntity.badRequest().body(new ErrorResponse("No    events in the given city as of now","400"));
        }

        List<EventDetailsResponse> geteventsList = new ArrayList<>();

        for (Event event : events) {
            EventDetailsResponse response = new EventDetailsResponse();
            response.setName(event.getName());
            response.setPerformer(event.getPerformer());
            response.setCity(event.getCity());
            response.setEventStartTime(event.getEventStartTime());
            response.setEventEndTIme(event.getEventEndTIme());
            response.setCategory(event.getCategory());
            geteventsList.add(response);
        }

        return ResponseEntity.ok().body(geteventsList);
    }

    // return  ResponseEntity.badRequest().body(new ErrorResponse("event is null or empty exception ","400"));


    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateEventDetails(@PathVariable int id,

                                                @RequestBody(required = false) UpdateEventRequestDTO updaterequest) {
        if (updaterequest == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("  event  null is passed", "400"));
        }
        Event event;
        try {
          event  = eventservice.updateEventDetails(id, updaterequest);
            return ResponseEntity.ok().body(event);
        }catch(EventNotFoundException ev){
            return ResponseEntity.badRequest().body(new ErrorResponse("Event is not present with id","400") );
        }



    }

}




