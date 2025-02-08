package com.example.eventifyeventmanagment.scheduler;

import com.example.eventifyeventmanagment.entity.EventTicketsDetails;
import com.example.eventifyeventmanagment.entity.UserTicket;
import com.example.eventifyeventmanagment.loaders.PaymentStatusLoader;
import com.example.eventifyeventmanagment.repository.EventTicketRepository;
import com.example.eventifyeventmanagment.repository.UserBookedTicketRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RestController
@RequestMapping("api/scheduler")
public class checkPaymentScheduler {
    @Autowired
    private UserBookedTicketRepository ticketRepository;
    @Autowired
    private EventTicketRepository eventTicketRepository;
    @Autowired
    private PaymentStatusLoader paymentStatusLoader;
    @Value("${checkPaymentSchedulerTime}")
    private Integer Xmins;
    Logger logger = LoggerFactory.getLogger(checkPaymentScheduler.class);

    // @Scheduled(fixedRate = 100000) // Runs every 5 minutes
    @GetMapping("/checkpendingpayments")
    public void checkUnconfirmedPayments() throws StripeException {
        Integer paymentStatusId = paymentStatusLoader.getPaymentStatusIdByUsingStatusName("pending");
        logger.info("pending status Id is " + paymentStatusId);
        LocalDateTime timeLimit = LocalDateTime.now().minusMinutes(Xmins);

        List<UserTicket> pendingTickets = ticketRepository.findPendingPayments(paymentStatusId, timeLimit);
        logger.info("No of pending tickets: {}", pendingTickets.size());

        for (UserTicket ticket : pendingTickets) {
            logger.info("pending ticket {}", ticket.getId());
            PaymentIntent intent = PaymentIntent.retrieve(ticket.getPaymentIntentId());

            if (!"succeeded".equals(intent.getStatus())) {
                logger.info("ticketID {} has paymentIntentId {} status {}", ticket.getId(), ticket.getPaymentIntentId(), intent.getStatus());
                Long eventId = (ticket.getEvent().getId());
                Integer evIDIntVal = eventId.intValue();
                Integer noOfSeats = ticket.getSeatsBooked();
                Optional<EventTicketsDetails> optionalEventTicketsDetails = eventTicketRepository.findByEventId(evIDIntVal);
                if (optionalEventTicketsDetails.isPresent()) {
                    logger.info("event ticket details is present");
                    EventTicketsDetails ticketsDetails = optionalEventTicketsDetails.get();
                    logger.info("before updating ticket count:" + ticketsDetails.getAvailableTickets());
                    Integer totalNewTicketCount = ticketsDetails.getAvailableTickets() + noOfSeats;
                    logger.info("total new ticketcount" + totalNewTicketCount);
                    ticketsDetails.setAvailableTickets(totalNewTicketCount);
                    Integer statusId = paymentStatusLoader.getPaymentStatusIdByUsingStatusName("cancelled");
                    logger.info("cancelled status  id :" + statusId);
                    ticket.setPaymentStatusId(statusId);
                    ticketRepository.save(ticket);
                    eventTicketRepository.save(ticketsDetails);
                }
                System.out.println("Cancelled unpaid ticket: " + ticket.getId());
            } else {
                logger.error("ticketID {} has success paymentIntentId {} ", ticket.getId(), ticket.getPaymentIntentId());
                Integer statusId = paymentStatusLoader.getPaymentStatusIdByUsingStatusName("confirmed");
                ticket.setPaymentStatusId(statusId);
                ticketRepository.save(ticket);

            }
        }
    }

}
