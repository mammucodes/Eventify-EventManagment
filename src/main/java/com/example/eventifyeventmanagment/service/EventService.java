package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.*;

import com.example.eventifyeventmanagment.dto.request.CreateEventRequestDTO;
import com.example.eventifyeventmanagment.dto.request.UpdateEventRequestDTO;
import com.example.eventifyeventmanagment.dto.request.UpdateEventTicketsRequestDTO;
import com.example.eventifyeventmanagment.dto.response.CreateEventResponse;
import com.example.eventifyeventmanagment.dto.response.EventTicketResponse;
import com.example.eventifyeventmanagment.dto.request.GetEventDetailsFiltersByDto;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.EventTicketsDetails;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.entity.UserTicket;
import com.example.eventifyeventmanagment.loaders.EventStatusStaticLoader;
import com.example.eventifyeventmanagment.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EventService {
    Logger logger = LoggerFactory.getLogger(EventService.class);

    private final EventRepository eventrepository;
    private final UserRepository userrepository;
    private final EventTicketRepository ticketrepository;
    private final StatusRepository statusrepository;
    private final EventStatusStaticLoader staticLoader;
    private final UserBookedTicketRepository userBookedTicketRepository;


    private EmailService emailService;

    @Autowired
    public EventService(EventRepository eventrepository,
                        UserRepository userrepository,
                        EventTicketRepository ticketrepository,
                        StatusRepository statusrepository,
                        EventStatusStaticLoader staticLoader,
                        UserBookedTicketRepository userBookedTicketRepository,
                        EmailService emailService
    ) {

        this.eventrepository = eventrepository;
        this.userrepository = userrepository;
        this.ticketrepository = ticketrepository;
        this.statusrepository = statusrepository;
        this.staticLoader = staticLoader;
        this.userBookedTicketRepository = userBookedTicketRepository;
        this.emailService = emailService;

    }

    //THis method takes eventrequest object and   save it to the event database
    //and returns the event response
    //should not pass null object we are not handling it
    // if passed user is not found it  throws usernot found exception
    //if passed user to create an event is not organiser he cannot create event so it throws  UserFoundIsNotOrganizerException exception
    public void createEventWithResponse(CreateEventRequestDTO requestdto, CreateEventResponse eventresponse) throws UserNotFoundException, UserFoundIsNotOrganizerException, EventOverLapException, InvalidStatusOption {

        logger.info("Creating an event requestdto called");
        List<Event> events = eventrepository.findByPerformerAndFilters(
                requestdto.getPerformer(), requestdto.getEventstarttime(), requestdto.getEventendtime());

        if (!events.isEmpty()) {
            logger.info("THis eperformer is already doing an event ");

            Timestamp eventStartTime = events.get(0).getEventStartTime();
            Timestamp eventEndTime = events.get(0).getEventEndTime();
            throw new EventOverLapException("There is already event present with same performer at same time cannot create another event starts at "
                    + eventStartTime + " and ends at " + eventEndTime, "400");

        }

        Optional<User> optionalUser = userrepository.findById(Long.valueOf(requestdto.getOrganizerId()));
        User user = null;
        if (optionalUser.isPresent()) {
            user = optionalUser.get();
        } else {
            logger.info("given user is not present  so cannot create an event");
            throw new UserNotFoundException("no user with given organiser id is present in our system");
        }

        if (!user.isIsorganizer()) {
            logger.info("passed user is not an organiser so he cannot create an event");
            throw new UserFoundIsNotOrganizerException("passed  user is not an organiser . so cannot create an event");
        }
        //passed all validation you can create event now

        Event event = new Event();
        event.setName(requestdto.getName());
        event.setPerformer(requestdto.getPerformer());
        event.setCity(requestdto.getCity());
        event.setCategory(requestdto.getCategory());
        event.setEventStartTime(requestdto.getEventstarttime());
        event.setEventEndTime(requestdto.getEventendtime());
        event.setDescription(requestdto.getDescription());
        event.setOrganizerId(requestdto.getOrganizerId());


        String statusName = requestdto.getStatus();

        if (statusName == null || statusName.isEmpty()) {
            int statusId = staticLoader.getStatusIdByUsingStatusName("available");
            event.setStatusId(statusId);
        } else {
            Integer statusId = staticLoader.getStatusIdByUsingStatusName(statusName);
            if (statusId != null) {
                event.setStatusId(statusId);
            } else {
                throw new InvalidStatusOption("Invalid status option is passed", 400);
            }
        }


        Event savedEvent = eventrepository.save(event);
        logger.info(" successfully saved event details ");
        Long eventId = savedEvent.getId();

        eventresponse.setId(eventId);
        eventresponse.setName(savedEvent.getName());
        eventresponse.setCity(savedEvent.getCity());
        eventresponse.setEventstarttime(savedEvent.getEventStartTime());
        eventresponse.setEventendtime(savedEvent.getEventEndTime());
        eventresponse.setPerformer(savedEvent.getPerformer());
        eventresponse.setCategory(savedEvent.getCategory());
        eventresponse.setOrganizerId(savedEvent.getOrganizerId());
        eventresponse.setStatusId(savedEvent.getStatusId());
        logger.info("sucessfully set the event details in eventresponse object");

        if (requestdto.getEventTicketDetails() != null) {
            EventTicketsDetails eventtickets = new EventTicketsDetails();
            logger.info("setting passed  requestdto ticket details to even");
            eventtickets.setAvailableTickets((requestdto.getEventTicketDetails().getAvailableTickets()));
            eventtickets.setTicketPrice(requestdto.getEventTicketDetails().getTicketPrice());
            eventtickets.setMaxTicektsCanBook(requestdto.getEventTicketDetails().getMaxTicketsCanBook());
            eventtickets.setEventId(eventId);


            EventTicketsDetails eventTickets = ticketrepository.save(eventtickets);
            logger.info("successfully saved  eventtickets details toEventTickets table");


            EventTicketResponse eventTicketResponse = new EventTicketResponse();

            eventTicketResponse.setId(eventTickets.getId());
            eventTicketResponse.setAvailableTickets(eventTickets.getAvailableTickets());
            eventTicketResponse.setTicketPrice(eventTickets.getTicketPrice());
            eventTicketResponse.setMaxTicektsCanBook(eventTickets.getMaxTicektsCanBook());
            eventresponse.setEventTicketResponse(eventTicketResponse);
            logger.info("sucessfully created an event and set the eventresponse with values");

        }


    }


    //This method takes input city as String and GetEventDetaildDTo object
    //and it returns list of  events present in that city
    //if no events present it returns empty list
//should give valid city  name  cannot pass null we are not handling it
    public List<Event> getEventDetails(String city, GetEventDetailsFiltersByDto geteventsrequest) throws InvalidStatusOption {
        List<Event> events = null;
        if (geteventsrequest == null) {
            logger.info("get events details by just city name");
            events = eventrepository.findByCity(city);
            return events;
        }

        String eventName = geteventsrequest.getName();
        String performer = geteventsrequest.getPerformer();
        LocalDate eventStartDate = geteventsrequest.getEventStartDate();
        String statusName = geteventsrequest.getStatus();
        String category = geteventsrequest.getCategory();
        Integer statusId;
        if (statusName == null || statusName.isEmpty()) {
            statusId = staticLoader.getStatusIdByUsingStatusName("available");
        } else {
            statusId = staticLoader.getStatusIdByUsingStatusName(statusName);
            if (statusId == null) {

                throw new InvalidStatusOption("Invalid status option is passed", 400);
            }
        }

        String performerParam = (performer == null) ? "%" : "%" + performer + "%";
        String nameParam = (eventName == null) ? "%" : "%" + eventName + "%";
        String categoryParam = (category==null)?"%" : "%" + category + "%";

        if (eventStartDate == null) {
            events = eventrepository.findByCityAndFilters(city, performerParam, nameParam,categoryParam, statusId);
            return events;
        }
        // Calculate start and end of the given date
        LocalDateTime startDate = eventStartDate.atStartOfDay(); // 12:00 a.m.
        LocalDateTime endDate = eventStartDate.atTime(23, 59, 59); // 11:59 p.m.


        events = eventrepository.findByCityAndFilters(city, performerParam, nameParam,categoryParam, startDate, endDate, statusId);
        return events;
    }

    // This method takes event id as input and updaterquestdto as object which has  values to be updated
    //and returns  Event object  with updated values
    //should give valid id yu cannot pass null we are not handling it
    //  if no events present with that id it htrows EventNotFoundException
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
                event.setEventEndTime(updateeventrequestdto.getEventendtime());
            if(updateeventrequestdto.getDescription()!=null)
                event.setDescription(updateeventrequestdto.getDescription());
            // Save the updated entity
            return eventrepository.save(event);
        } else {
            throw new EventNotFoundException("Event not found with ID: " + id);
        }
    }

    //This method takes eventid and organiserId as input
    //if event is not present it throws event nt found Exception
    // if event is present but correspondin organiserID is not present in that event
    //it throws  EventWithGivenOrganizerIsNotPresentException Exceptions
    // if both are present then it scuessfully delete the event
    //if for any Case event id and oraganiser id null is passed it will throw IllegealrgumentExceptiom
    public void cancelAnEvent(int eventId, Integer organiserId) throws
            EventNotFoundException,
            EventWithGivenOrganizerIsNotPresentException,
            EventAlreadyCancelledException {
        logger.info("trying to check if event present in database");
        Optional<Event> optionalEvent = eventrepository.findById((long) eventId); // it returns empty event .even though no events present so first convert to Event object
        Event event = null;
        if (optionalEvent.isPresent()) {
            event = optionalEvent.get();
        } else {
            logger.info("no event present in database");
            throw new EventNotFoundException("no such event with given eventid present" + eventId);
        }

        if (!event.getOrganizerId().equals(organiserId)) {
            logger.info("passed organised id is not present in the corresponding event cannot delete the event");
            throw new EventWithGivenOrganizerIsNotPresentException("event  with  given organiser is not present");
        }
        Integer passedStatusId = event.getStatusId();
        String passedStatusName = staticLoader.getStatusNameByUsingStatusId(passedStatusId);
        if (passedStatusName.equals("cancelled")) {
            throw new EventAlreadyCancelledException("this event is already cancelled no need to cancel again");
        }
        String statusName = "cancelled";
        Integer statusId = staticLoader.getStatusIdByUsingStatusName(statusName);
        event.setStatusId(statusId);
        eventrepository.save(event);

        List<UserTicket> userTickets = userBookedTicketRepository.findTicketsByEventId(eventId);
        for (UserTicket user : userTickets) {
            String email = user.getUser().getEmail();
            String body = buildEmailBody(user);
//sending email to users who booked  ticket saying event cancelled
            emailService.sendEmail(email, "The following event is cancelled", body);
//todo need to do payment gateway for refund the money
        }
        logger.info("cancelled  the event succesfully");


    }
//this method helps you to build email body
    //it takes userticket object
 //   and retruns  a body of type string with all event details  which you want to send in body
    private String buildEmailBody(UserTicket user) {

        String newLine = "<br>";
        String body = "Event  " + user.getEvent().getName() + " is cancelled  we will refund the money" + newLine;
        body += "Event Name: " + user.getEvent().getName() + newLine;
        body += " performer :" + user.getEvent().getPerformer() + newLine;
        body += "city :" + user.getEvent().getCity() + newLine;
        body += "eventStartDate :" + user.getEvent().getEventStartTime() + newLine;
        System.out.println(body);
        return body;
    }


//If Ticket price  is passed as 0 it means free event
    //This method is used to update event ticket details
    //it take input as eventid and updateeventticket object
    //
   //  and returns updated EventTicketsDetail object

    public EventTicketsDetails UpdateEventTicketDetails(Integer eventId, UpdateEventTicketsRequestDTO updateEventTicketsRequestDTO) throws
                                             EventNotFoundException,
                                     UserFoundIsNotOrganizerException,
                                   EventTicketsOrTicketsPriceNotFoundException{

        Optional<Event> optionalEvent = eventrepository.findById(Long.valueOf(eventId));
        Event event = null;
        if (optionalEvent.isPresent()) {
            event = optionalEvent.get();
        } else {
            throw new EventNotFoundException("no event found with given eventID");
        }

        if (!Objects.equals(event.getOrganizerId(), updateEventTicketsRequestDTO.getOrganizerId())) {
            logger.info("not an organizer cannot update ticekt details");
            throw new UserFoundIsNotOrganizerException("passed organizerId is not an an oraganizer so, cannot update ticekt details");
        }
        Optional<EventTicketsDetails> ticketsDetails = ticketrepository.findByEventId(eventId);

        if (ticketsDetails.isPresent()) {
            EventTicketsDetails eventTicketsDetails = ticketsDetails.get();
            Integer oldTicketCount = eventTicketsDetails.getAvailableTickets();

            Integer newTicketCount = updateEventTicketsRequestDTO.getNewTicketsAdded();
            Integer totalTickets = oldTicketCount + newTicketCount;

            eventTicketsDetails.setAvailableTickets(totalTickets);
            Integer eventTicketPrice = updateEventTicketsRequestDTO.getNewTicketPrice();
            if (eventTicketPrice != null) {
                eventTicketsDetails.setTicketPrice(eventTicketPrice);
            }
            Integer maxTicketsPerUserForEvent = updateEventTicketsRequestDTO.getMaxTicketsPerUser();
            if (maxTicketsPerUserForEvent != null) {
                eventTicketsDetails.setMaxTicektsCanBook(maxTicketsPerUserForEvent);
            }
            ticketrepository.save(eventTicketsDetails);
            return eventTicketsDetails;

        } else {
            Integer ticketPrice = updateEventTicketsRequestDTO.getNewTicketPrice();
            Integer ticketsCount = updateEventTicketsRequestDTO.getNewTicketsAdded();
            Integer maxTicketsPerUser = updateEventTicketsRequestDTO.getMaxTicketsPerUser();

            EventTicketsDetails ticketDetails = new EventTicketsDetails();
            if (ticketPrice == null || ticketsCount == null) {
                throw new EventTicketsOrTicketsPriceNotFoundException("nll value passed for  tickets count and ticekts price");
            }
            if (maxTicketsPerUser == null) {
                ticketDetails.setMaxTicektsCanBook(1);
            }
            ticketDetails.setAvailableTickets(ticketsCount);
            ticketDetails.setTicketPrice(ticketPrice);
            ticketDetails.setMaxTicektsCanBook(maxTicketsPerUser);
            EventTicketsDetails eventTicketDetails = ticketrepository.save(ticketDetails);

            return eventTicketDetails;
        }


    }


}


