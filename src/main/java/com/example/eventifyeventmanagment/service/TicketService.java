package com.example.eventifyeventmanagment.service;

import com.example.eventifyeventmanagment.Exceptions.*;
import com.example.eventifyeventmanagment.dto.request.EventBookingRequest;
import com.example.eventifyeventmanagment.dto.request.PaymentConfirmRequest;
import com.example.eventifyeventmanagment.dto.response.EventDetailsResponse;
import com.example.eventifyeventmanagment.dto.response.UserDetailsResponse;
import com.example.eventifyeventmanagment.dto.request.BookEventTicketRequestDTO;
import com.example.eventifyeventmanagment.dto.response.UserTicketResponseDTO;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.EventTicketsDetails;
import com.example.eventifyeventmanagment.entity.User;
import com.example.eventifyeventmanagment.entity.UserTicket;
import com.example.eventifyeventmanagment.loaders.EventStatusStaticLoader;
import com.example.eventifyeventmanagment.loaders.PaymentStatusLoader;
import com.example.eventifyeventmanagment.repository.*;
import com.stripe.Stripe;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Map;
import java.util.Optional;

@Service
public class TicketService {
    @Value("${stripe.api.key}")
    private String stripeApiKey;
    Logger logger = LoggerFactory.getLogger(UserService.class);
    private EventRepository eventRepository;
    private final EventTicketRepository eventTicketRepository;
    private final UserBookedTicketRepository bookedTicketRepository;
    private final UserRepository userRepository;
    private final EventStatusStaticLoader staticLoader;
    private final PaymentStatusLoader paymentStatusLoader;


    @Autowired
    public TicketService(EventRepository eventRepository,
                         EventTicketRepository eventTicketRepository,
                         UserBookedTicketRepository bookedTicketRepository,
                         UserRepository userRepository,
                         EventStatusStaticLoader staticLoader,
                         PaymentStatusLoader paymentStatusLoader) {

        this.eventRepository = eventRepository;
        this.eventTicketRepository = eventTicketRepository;
        this.bookedTicketRepository = bookedTicketRepository;
        this.userRepository = userRepository;
        this.staticLoader = staticLoader;
        this.paymentStatusLoader = paymentStatusLoader;
    }

    //todo we can check if event or event tickets details or user details   present in data base parallely by using threads . do it later
    //This method take eventID and ookeventticket object as inputs
    //and books  ticket to an event  if tickets are available and user is already present in db
    //retruns UserTicket object with all event ticket details
    //throws eventticketdetailsnotfound, usernotfound,insufficentticketcount,  exception

    public UserTicket bookEventTicket(Integer eventId, BookEventTicketRequestDTO bookEventTicketRequestDTO) throws EventNotFoundException, EventTicketDetailsNotFOundException, InsufficientTicketsAvailableException, UserNotFoundException, PassedTicketCountIsMoreThanLimitException, NoOfSeatsToBookNotFoundException, StripeException {
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
        if (passedNoOfTicketsToBook == null) {
            throw new NoOfSeatsToBookNotFoundException("need to pass no of seats to be booked");
        }
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
        Integer ticketPrice = passedNoOfTicketsToBook * eventTicketsDetails.getTicketPrice();

        String paymentIntentId = createPaymentIntentId(ticketPrice);

        userTicketDetails.setPaymentIntentId(paymentIntentId);


        userTicketDetails.setUser(user);
        userTicketDetails.setSeatsBooked(bookEventTicketRequestDTO.getNoOfSeats());
        // userTicketDetails.setCheckInCount(0); // intail default values
        // userTicketDetails.setCheckOutCount(0); // intial default values
        userTicketDetails.setTicketBookedOn(LocalDateTime.now());

        Integer paymentStatusId = paymentStatusLoader.getPaymentStatusIdByUsingStatusName("pending");
        userTicketDetails.setPaymentStatusId(paymentStatusId);

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


    public String createPaymentIntentId(Integer ticketPriceInDollars) throws StripeException {
        // Step 1: Create a PaymentIntent in Stripe


        Stripe.apiKey = stripeApiKey; // Ensure API key is set before calling Stripe

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(ticketPriceInDollars * 100L) // Amount in cents (e.g., 1000 = $10.00)
                .setCurrency("usd")
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                .setEnabled(true) // Let Stripe handle payment methods
                                .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                .build()
                )
                .build();

        PaymentIntent pi = PaymentIntent.create(params);
        String paymentIntentId = pi.getId();
        return paymentIntentId;
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

    // todo checkIn and check out apis should take event start time and end time into considration like allow check in beofre 2 hrs of start of event and dont allow after event time is crossed
//
//
    public void getCheckIn(Integer ticketId) throws UserBookedTicketDetailsNotFounException, InvalidStatusOption, EventCheckInTimeIsBeforeAllowedcheckInTimeException {

        Optional<UserTicket> optionalUserTicket = bookedTicketRepository.findById(ticketId);
        if (optionalUserTicket.isPresent()) {
            logger.info("user ticket is present");
            UserTicket userTicket = optionalUserTicket.get();
            LocalDateTime eventStartTime = userTicket.getEvent().getEventStartTime().toLocalDateTime();
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime checkInAllowedTime = eventStartTime.minusHours(3);
            //  if(currentTime.isAfter(eventEndTime)){
//                throw new EventAlreadyFinishedException("event you are trying to check in is already crossed cant allow");
//            } //not required let if customer wants to come late we will allow
            //if(currentTime.isEqual(checkInAllowedTime)|| currentTime.isAfter((checkInAllowedTime))
            //you can write above logic as if currentTime is not before check In allowed time we allow to check in
//
            if (!currentTime.isBefore(checkInAllowedTime)) {

                Integer checkInCount = userTicket.getCheckInCount();
                checkInCount++;
                userTicket.setCheckInCount(checkInCount);
                bookedTicketRepository.save(userTicket);
                logger.info("sucessfully updated check in count of the event");
            } else {
                throw new EventCheckInTimeIsBeforeAllowedcheckInTimeException("event start time is before  allowed check in time . cant check in now . check in allowed only 3 hrs before event start time ");
            }

        } else {
            throw new UserBookedTicketDetailsNotFounException("passed tickets details are not present");
        }


        // bookedTicketRepository.save(userTicket);

    }

    public void getCheckOut(Integer ticketId) throws UserBookedTicketDetailsNotFounException {

        Optional<UserTicket> optionalUserTicket = bookedTicketRepository.findById(ticketId);
        if (optionalUserTicket.isPresent()) {
            logger.info("user ticket is present");
            UserTicket userTicket = optionalUserTicket.get();
            Integer checkOutCount = userTicket.getCheckOutCount();
            if (checkOutCount == null) {
                checkOutCount = 0;
            }
            checkOutCount++;
            userTicket.setCheckOutCount(checkOutCount);
            bookedTicketRepository.save(userTicket);
            logger.info("sucessfully updated check out count of the event");
        } else {
            throw new UserBookedTicketDetailsNotFounException("passed tickets details are not present");
        }


    }

    public UserTicket confirmPayment(PaymentIntent paymentIntent, PaymentConfirmRequest request) throws Exception {


        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(request.getPaymentMethodId()) // Attach payment method
                .build();

        paymentIntent.setPaymentMethod(request.getPaymentMethodId());

        PaymentIntent confirmedPaymentIntent = null;
        try {
            confirmedPaymentIntent = paymentIntent.confirm(params);
        }
        catch(InvalidRequestException ire){
            logger.error("stripe exception occured failed to confirm payment "+ire.getMessage());
            throw new PaymentFailedException("stripe exception occured failed to confirm payment"+ ire.getMessage()+" "+ire.getClass().getName());
        }
        catch(Exception e){
            logger.error("Unknown Exception occured "+e.getMessage());
            throw new Exception("unknown exception occured"+e.getMessage());
        }

        //todo not always a PaymentIntent object.. fix later
       // PaymentIntent confirmedPaymentIntent = testStripeApisForPayments(request.getPaymentIntentId(), request.getPaymentMethodId());
//if(confirmedPaymentIntent!=null) {
    String paymentStatus = confirmedPaymentIntent.getStatus();


        UserTicket ticket = bookedTicketRepository.findByPaymentIntentId(request.getPaymentIntentId());
        // Step 1: Check Payment Status
        if ("succeeded".equals(paymentStatus)) {
            // Step 2: Update payment status in DB

            Integer paymentStatusId = paymentStatusLoader.getPaymentStatusIdByUsingStatusName("confirmed");
            ticket.setPaymentStatusId(paymentStatusId);
            ticket = bookedTicketRepository.save(ticket);

        } else {
            //  throw new PaymentFailedException("cannot complete the payment. payment time exceeded");
            if (paymentStatus.equals("canceled")) {
                Integer paymentStatusId = paymentStatusLoader.getPaymentStatusIdByUsingStatusName(paymentStatus);
                ticket.setPaymentStatusId(paymentStatusId);
                Long eventId = ticket.getEvent().getId();
                Optional<EventTicketsDetails> optionalEventTicketsDetails = eventTicketRepository.findByEventId(eventId.intValue());
                if (optionalEventTicketsDetails.isPresent()) {
                    EventTicketsDetails eventTicketsDetails = optionalEventTicketsDetails.get();
                    Integer newAvaiableTickets = eventTicketsDetails.getAvailableTickets() + ticket.getSeatsBooked();
                    eventTicketsDetails.setAvailableTickets(newAvaiableTickets);
                    eventTicketRepository.save(eventTicketsDetails);
                } else {
                    throw new EventTicketDetailsNotFOundException("no event tiket deetails with given event id");//todo  exception response should be with paymentIntendId ,paymentstatus
                }

            }


        }
        return ticket;
    }


    public Object testStripeApisForPayments(String paymentIntendId, String paymentMethodId){

        PaymentIntent intent = null;
        try {
            intent = PaymentIntent.retrieve(paymentIntendId);
        } catch (StripeException e) {
           logger.error("PaymentIntent.retrieve failed with StripeException {}", e.getMessage());
           return "PaymentIntent.retrieve failed";
        }
        String paymentStatus = intent.getStatus();
        logger.info("payment status before  confirm api called {}", paymentStatus);

        PaymentIntentConfirmParams params = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(paymentMethodId) // Attach payment method
                .build();


        PaymentIntent confirmedPaymentIntent = null;
        try {
            confirmedPaymentIntent = intent.confirm(params);
            logger.info("payment status after confirm api called {}", confirmedPaymentIntent.getStatus());
        } catch (Exception e) {
            logger.info("Exception occured while creating confirm payment {} {}", e.getClass().getName(), e.getMessage());
            return "stripe confirm failed with exception";
        }
        return confirmedPaymentIntent;
    }

}




