package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.Exceptions.*;
//import com.example.eventifyeventmanagment.dto.EventDTO;
//import com.example.eventifyeventmanagment.dto.EventResponse;
import com.example.eventifyeventmanagment.dto.request.CreateEventRequestDTO;
import com.example.eventifyeventmanagment.dto.request.GetEventDetailsFiltersByDto;
import com.example.eventifyeventmanagment.dto.request.UpdateEventRequestDTO;
import com.example.eventifyeventmanagment.dto.request.UpdateEventTicketsRequestDTO;
import com.example.eventifyeventmanagment.dto.response.*;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.EventTicketsDetails;
import com.example.eventifyeventmanagment.loaders.EventStatusStaticLoader;
import com.example.eventifyeventmanagment.repository.EventRepository;
import com.example.eventifyeventmanagment.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("api/events")
public class EventController {

    private EventService eventservice;
    Logger logger = LoggerFactory.getLogger(EventController.class);
    private EventStatusStaticLoader staticLoader;

    @Autowired
    public EventController(EventService eventservice,EventStatusStaticLoader staticLoader) {

        this.eventservice = eventservice;
        this.staticLoader = staticLoader;


    }

    @PostMapping("/create")
    public ResponseEntity<?> createEvent(@RequestBody CreateEventRequestDTO eventrequestdto) throws InvalidStatusOption {

        ResponseEntity<ErrorResponse> badRequest = validateCreateEventDTO(eventrequestdto);
        if (badRequest != null)
            return badRequest;

        // Proceed with event creation  if all validations pass
        try {
            CreateEventResponse responsedto = new CreateEventResponse();
            eventservice.createEventWithResponse(eventrequestdto, responsedto);

            return ResponseEntity.ok().body(responsedto);
        } catch (UserNotFoundException ue) {
            return ResponseEntity.badRequest().body(new ErrorResponse("no user with given organiser id is present in our system", "400"));
        } catch (UserFoundIsNotOrganizerException eo) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Passed user is not an organiser . so  cannot create an event", "400"));
        } catch (EventOverLapException eo) {
            return ResponseEntity.badRequest().body(new ErrorResponse(eo.getMessage(), eo.getCode()));
        }
    }

    @GetMapping("/{city}")
    public ResponseEntity<?> getEventDetails(
            @PathVariable String city,
            @RequestBody(required = false) GetEventDetailsFiltersByDto geteventrequest
    ) throws InvalidStatusOption {
        List<Event> events;

        if (city == null || city.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("City name cannot be null or empty", "400"));
        }
        events = eventservice.getEventDetails(city, geteventrequest);
        if (events == null || events.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("No    events in the given city as of now", "400"));
        }

        List<EventDetailsResponse> geteventsList = new ArrayList<>();

        for (Event event : events) {
            EventDetailsResponse response = new EventDetailsResponse();
            response.setName(event.getName());
            response.setPerformer(event.getPerformer());
            response.setCity(event.getCity());
            response.setEventStartTime(event.getEventStartTime());
            response.setEventEndTIme(event.getEventEndTime());
            response.setCategory(event.getCategory());
            response.setOrganizerId(event.getOrganizerId());
            response.setDescription(event.getDescription());

            Integer statusId = event.getStatusId();
            String status =  staticLoader.getStatusNameByUsingStatusId(statusId);
            response.setStatus(status);

            geteventsList.add(response);
        }

        return ResponseEntity.ok().body(geteventsList);
    }

    // return  ResponseEntity.badRequest().body(new ErrorResponse("event is null or empty exception ","400"));


    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateEventDetails(@PathVariable int id,

                                                @RequestBody(required = false) UpdateEventRequestDTO updaterequest) throws EventNotFoundException {
        if (updaterequest == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("  event  null is passed", "400"));
        }
        Event event;

        event = eventservice.updateEventDetails(id, updaterequest);
        //catching if any exception occurs in GlobalHandlerException if Event id is not foumd
        return ResponseEntity.ok().body(event);


    }

    // To Do need to generate oraganiserId from JWt token
    //Request param is also known as Query Param
    @PutMapping("/cancel/{eventId}")
    public ResponseEntity<?> cancelAnEvent(@PathVariable int eventId,
                                           @RequestHeader Integer organizerId)  throws EventAlreadyCancelledException{

        if (eventId == 0||organizerId==null||organizerId==0) {

            return ResponseEntity.badRequest().body(new ErrorResponse("id cannot be zero or null", "400"));
        }


        //need to generate user id through jwt token
        try {
            eventservice.cancelAnEvent(eventId, organizerId);
            return ResponseEntity.ok().body("Succesffully cancelled the event mail ");
        } catch (EventNotFoundException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Event with given id is not present", "400"));
        } catch (EventWithGivenOrganizerIsNotPresentException eo) {
            return ResponseEntity.badRequest().body(new ErrorResponse("No  matching event with given organiserId is present", "400"));
        }
    }
@PutMapping("/updateticket/{eventId}")
    public ResponseEntity<?> updateEventTicketDetails(@PathVariable Integer eventId,
            @RequestBody UpdateEventTicketsRequestDTO updateEventTicketsRequestDTO) throws EventTicketsPerUserPassedZeroException, EventNotFoundException, EventTicketsOrTicketsPriceNotFoundException, UserFoundIsNotOrganizerException {
        ResponseEntity<ErrorResponse> badRequest = validateUpdateEventTicketDetails(eventId,updateEventTicketsRequestDTO);
        if (badRequest != null)
            return badRequest;
        EventTicketsDetails eventTicketDetails = eventservice.UpdateEventTicketDetails(eventId,updateEventTicketsRequestDTO);

        UpdateEventTicketResponse updateEventTicketResponse = new UpdateEventTicketResponse();
        updateEventTicketResponse.setMessage("sucessfully updated event tickets details");

        updateEventTicketResponse.setEventId(eventTicketDetails.getEventId());
        updateEventTicketResponse.setTotalTickets(eventTicketDetails.getAvailableTickets());
        updateEventTicketResponse.setNewTicketPrice(eventTicketDetails.getTicketPrice());
        updateEventTicketResponse.setMaxTicketsPerUser(eventTicketDetails.getMaxTicektsCanBook());
        return ResponseEntity.ok(updateEventTicketResponse);

    }

    public ResponseEntity<ErrorResponse> validateUpdateEventTicketDetails(Integer eventId, UpdateEventTicketsRequestDTO updateEventTicketsRequestDTO) {
        if (updateEventTicketsRequestDTO == null) {
            logger.info("null updateEventTicketsRequestDTO object is passed");
            return ResponseEntity.badRequest().body(new ErrorResponse("cannot pass null null updateEventTicketsRequestDTO ", "400"));
        }
        if (eventId == null || eventId<= 0) {
            logger.info("eventId cannot be null or zero or negative number");
            return ResponseEntity.badRequest().body(new ErrorResponse("eventId cannot be null  or less than or equal to 0to update ticekts details we neeed eventId", "400"));
        }
        if (updateEventTicketsRequestDTO.getOrganizerId() == null || updateEventTicketsRequestDTO.getOrganizerId() <= 0) {
            logger.info("organizerId cannot be null or negative number");
            return ResponseEntity.badRequest().body(new ErrorResponse("organizerId cannot be null or less than or equal to 0 to update ticekt details", "400"));
        }
        if(updateEventTicketsRequestDTO.getNewTicketPrice() != null) {
            if (updateEventTicketsRequestDTO.getNewTicketPrice() < 0) {

                logger.info("ticekt price or count or maxticekts to be booked cannot be less than 0");
                return ResponseEntity.badRequest().body(new ErrorResponse("ticketprice or ticektcount or maxticekts allowed  values cannot be less than 0", "400"));
            }
        }

            if (updateEventTicketsRequestDTO.getNewTicketsAdded() == null ||  updateEventTicketsRequestDTO.getNewTicketsAdded() <= 0) {

                logger.info("ticekt price or count or maxticekts to be booked cannot be less than 0");
                return ResponseEntity.badRequest().body(new ErrorResponse("ticketprice or ticektcount or maxticekts allowed  values cannot be less than 0", "400"));
            }

        if(updateEventTicketsRequestDTO.getMaxTicketsPerUser() != null) {
            if (updateEventTicketsRequestDTO.getMaxTicketsPerUser() <= 0) {

                logger.info("ticekt price or count or maxticekts to be booked cannot be less than 0");
                return ResponseEntity.badRequest().body(new ErrorResponse("ticketprice or ticektcount or maxticekts allowed  values cannot be less than 0", "400"));
            }
        }
        return null;
    }


    private ResponseEntity<ErrorResponse> validateCreateEventDTO(CreateEventRequestDTO eventrequestdto) {

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
            return ResponseEntity.badRequest().body(new ErrorResponse("category name cannot be null or empty", "400"));
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

        if ((eventrequestdto.getEventTicketDetails().getTicketPrice() < 0) || (eventrequestdto.getEventTicketDetails().getAvailableTickets() < 0) || (eventrequestdto.getEventTicketDetails().getMaxTicketsCanBook() < 0)) {
            logger.info("ticekt pric or count or maxticekts to be booked cannot be less than 0");
            return ResponseEntity.badRequest().body(new ErrorResponse("ticket object values cannot be less than 0", "400"));
        }
        return null;
    }
}




