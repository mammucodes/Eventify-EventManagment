package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.*;
import com.example.eventifyeventmanagment.dto.EventDetailsResponse;
import com.example.eventifyeventmanagment.dto.UserDetailsResponse;
import com.example.eventifyeventmanagment.dto.request.BookEventTicketRequestDTO;
import com.example.eventifyeventmanagment.dto.UserTicketResponseDTO;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.EventTicketsDetails;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.entity.UserTicket;
import com.example.eventifyeventmanagment.loaders.EventStatusStaticLoader;
import com.example.eventifyeventmanagment.repository.EventRepository;
import com.example.eventifyeventmanagment.repository.EventTicketRepository;
import com.example.eventifyeventmanagment.repository.UserBookedTicketRepository;
import com.example.eventifyeventmanagment.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;

@Service
public class TicketService {
    Logger logger = LoggerFactory.getLogger(UserService.class);
    private EventRepository eventRepository;
    private final EventTicketRepository eventTicketRepository;
    private final UserBookedTicketRepository bookedTicketRepository;
    private final UserRepository userRepository;
    private final EventStatusStaticLoader staticLoader;


    @Autowired
    public TicketService(EventRepository eventRepository,
                         EventTicketRepository eventTicketRepository,
                         UserBookedTicketRepository bookedTicketRepository,
                         UserRepository userRepository,
                         EventStatusStaticLoader staticLoader) {

        this.eventRepository = eventRepository;
        this.eventTicketRepository = eventTicketRepository;
        this.bookedTicketRepository = bookedTicketRepository;
        this.userRepository = userRepository;
        this.staticLoader = staticLoader;
    }

    //todo we can check if event or event tickets detaild or user details   present in data base parallely by using threads . do it later
    public UserTicket bookEventTicket(Integer eventId, BookEventTicketRequestDTO bookEventTicketRequestDTO) throws EventNotFoundException, EventTicketDetailsNotFOundException, InsufficientTicketsAvailableException, UserNotFoundException, PassedTicketCountIsMoreThanLimitException {
        Optional<Event> optionalEvent = eventRepository.findById(Long.valueOf(eventId));
        Event event = null;
        if (optionalEvent.isPresent()) {
            logger.info("passed event   is present in Data base");
            event = optionalEvent.get();
        } else {
            logger.info("passed event  with given eventid " + eventId + " is not found in DB ");
            throw new EventNotFoundException("passed eventid " + eventId + "is not present in the event list");
        }
        Optional<EventTicketsDetails> optionalEventTicketsDetails = eventTicketRepository.findByEventId(eventId);
        logger.info("finding EventTicketDetails  from DataBase");

        EventTicketsDetails eventTicketsDetails = null;
        if (optionalEventTicketsDetails.isPresent()) {
            logger.info("found event ticket details from database");
            eventTicketsDetails = optionalEventTicketsDetails.get();

        } else {
            logger.info("could not found eventTicket details with given eventId" + eventId + " from Data Base");
            throw new EventTicketDetailsNotFOundException("there  are no event  tickets found for this eventId" + eventId);
        }
        Integer maxTicketAllowedPerTicket = eventTicketsDetails.getMaxTicektsCanBook();
        Integer passedNoOfTicketsToBook = bookEventTicketRequestDTO.getNoOfSeats();
        if (passedNoOfTicketsToBook > maxTicketAllowedPerTicket) {

            logger.info("passed ticket seat count " + passedNoOfTicketsToBook + "is more than  max seats allowed to booked" + maxTicketAllowedPerTicket);
            throw new PassedTicketCountIsMoreThanLimitException("passed ticket count is more than max ticket allowed");
        }

        Integer totalAvailableTickets = eventTicketsDetails.getAvailableTickets();


        if (totalAvailableTickets < passedNoOfTicketsToBook) {
            logger.info("Passed no of tickets" + passedNoOfTicketsToBook + " to be booked is more than  avaialble tickets" + totalAvailableTickets + " from event");
            throw new InsufficientTicketsAvailableException("Passed no of tickets to be booked is more than  avaialble tickets from event Exception");
        }
        UserTicket userTicketDetails = new UserTicket();

        userTicketDetails.setEvent(event); // this is required because in response we are gving complete  booked ticket details;
        logger.info("setting event details object to the userTicketDetails");
        Optional<User> optionalUser = userRepository.findById(Long.valueOf(bookEventTicketRequestDTO.getUserId()));

        User user = null;
        if (optionalUser.isPresent()) { //
            logger.info(" user with this id" + bookEventTicketRequestDTO.getUserId() + " is present in the database");

            user = optionalUser.get();
        } else {
            throw new UserNotFoundException("passed user id is not present in db " + bookEventTicketRequestDTO.getUserId());
        }

        userTicketDetails.setUser(user);
        userTicketDetails.setSeatsBooked(bookEventTicketRequestDTO.getNoOfSeats());
        userTicketDetails.setCheckInCount(0);
        userTicketDetails.setTicketBookedOn(LocalDateTime.now());

        UserTicket userTicket = bookedTicketRepository.save(userTicketDetails);
        logger.info("sucessfully saved user ticket details to database");

        Integer newAvailableTickets = totalAvailableTickets - passedNoOfTicketsToBook;
        logger.info("trying to set reamining avaible tickets in database");

        eventTicketsDetails.setAvailableTickets(newAvailableTickets);
        logger.info("set reamining avaible tickets in database");
        // eventTicketsDetails.setTicketBookedOn(LocalDateTime.now());

        eventTicketRepository.save(eventTicketsDetails);
        logger.info("succesfully saved update ticket count in event ticket details");
        return userTicket;
    }


    public UserTicketResponseDTO getEventTicketDetails(Integer ticketId) throws UserBookedTicketDetailsNotFounException, InvalidStatusOption {
        Optional<UserTicket> optionalUserTicket = bookedTicketRepository.findById(ticketId);

        if (optionalUserTicket.isPresent()) {
            logger.info("Event Ticket details are prent in DB with given id" + ticketId);
            UserTicket userTicket = optionalUserTicket.get();
            //System.out.println(ticket.getEvent());
            UserTicketResponseDTO userTicketResponse = new UserTicketResponseDTO();
            userTicketResponse.setTicketId(userTicket.getId());
            userTicketResponse.setSeatsBooked(userTicket.getSeatsBooked());
            userTicketResponse.setTicketBookedTime(userTicket.getTicketBookedOn());


            EventDetailsResponse event = new EventDetailsResponse();
            event.setName(userTicket.getEvent().getName());
            event.setCity(userTicket.getEvent().getCity());
            event.setPerformer(userTicket.getEvent().getPerformer());
            event.setEventStartTime(userTicket.getEvent().getEventStartTime());
            event.setEventEndTIme(userTicket.getEvent().getEventEndTime());
            event.setCategory(userTicket.getEvent().getCategory());
            event.setOrganizerId(userTicket.getEvent().getOrganizerId());

            Integer statusId = userTicket.getEvent().getStatusId();
            if (statusId == null || statusId <= 0) {
                throw new InvalidStatusOption("invalid statusoption is passed", 400);
            }

            String statusName = staticLoader.getStatusNameByUsingStatusId(statusId);
            event.setStatus(statusName);


            userTicketResponse.setEvent(event);

            UserDetailsResponse user = new UserDetailsResponse();
            user.setId(userTicket.getUser().getId().intValue());
            user.setName(userTicket.getUser().getName());
            user.setEmail(userTicket.getUser().getEmail());
            user.setRegister_on(userTicket.getUser().getRegisteredOn());

            // Calculate the difference
            //to store account active days
            LocalDate currentDate = LocalDate.now();
            Period period = Period.between(LocalDate.from(userTicket.getUser().getRegisteredOn()), currentDate);
            int AccountdaysActive = period.getYears() * 365 + period.getMonths() * 30 + period.getDays(); // Approximate calculation

            user.setAccountDays((long) AccountdaysActive);
            userTicketResponse.setUser(user);
            userTicketResponse.setCheckInCount(userTicket.getCheckInCount());

            logger.info("set all the data to ticketresponse object");
            return userTicketResponse;
        } else {
            throw new UserBookedTicketDetailsNotFounException("no event tickets details with given ticket Id" + ticketId);
        }
    }
}
