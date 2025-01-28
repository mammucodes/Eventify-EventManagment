package com.example.eventifyeventmanagment.scheduler;

import com.example.eventifyeventmanagment.dto.request.UserTicketDTO;
import com.example.eventifyeventmanagment.entity.Event;
import com.example.eventifyeventmanagment.entity.UserTicket;
import com.example.eventifyeventmanagment.loaders.EventStatusStaticLoader;
import com.example.eventifyeventmanagment.repository.EventRepository;
import com.example.eventifyeventmanagment.repository.UserBookedTicketRepository;
import com.example.eventifyeventmanagment.repository.UserRepository;
import com.example.eventifyeventmanagment.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RemainderScheduler {
    Logger logger = LoggerFactory.getLogger(RemainderScheduler.class);
    @Autowired
    private UserBookedTicketRepository userTicketRepository;
    private UserRepository userRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private EventStatusStaticLoader statusStaticLoader;


    //  @Autowired
    // private EmailService emailService;
   // @Scheduled(fixedRate = 5 * 60 * 1000)
    public void sendRemainders() {
        logger.info("Running ReminderScheduler at {}", LocalDateTime.now());
        try {
//            LocalDate today = LocalDate.now();
//            LocalDateTime startDayTime = today.atStartOfDay();
//            LocalDateTime endDateTime = today.atTime(LocalTime.MAX);


            LocalDate today = LocalDate.now();

            // Get the next day's start (00:00)
            // LocalDateTime nextDayStart = today.plusDays(1).atStartOfDay();
            LocalDateTime thirtyDaysBack = today.minusDays(30).atStartOfDay();


            // Get the next day's end (23:59:59.999999999)
            // LocalDateTime nextDayEnd = today.plusDays(1).atTime(LocalTime.MAX);
            LocalDateTime thirtydaysUpcoming = today.plusDays(30).atTime(LocalTime.MAX);
            Integer statusId =  statusStaticLoader.getStatusIdByUsingStatusName("available");

            // List<UserTicket> userEventsForRemainder = userTicketRepository.findUsersToSendTodaysEventRemainders(startDayTime, endDateTime);
//            List<UserTicket> userEventsForRemainder = userTicketRepository.
//                    findUsersToSendTodaysEventRemainders(thirtyDaysBack, thirtydaysUpcoming,statusId);

            List<UserTicketDTO> userTomorrowEventsForRemainder = userTicketRepository.
                    findUsersToSendNextDayEventRemainders(thirtyDaysBack, thirtydaysUpcoming,statusId);

            //making  map fpr same user who has mutlpile events on the same day will send reaminder to all thoseevents at a time
            Map<String, List<UserTicketDTO>> eventsByUser = userTomorrowEventsForRemainder.stream().collect(Collectors.groupingBy(
                    (eventsremainderForUser) -> eventsremainderForUser.getEmail())
            );
            logger.info("found this many events " + userTomorrowEventsForRemainder.size());

            for (Map.Entry<String, List<UserTicketDTO>> entry : eventsByUser.entrySet()) {
                String email = entry.getKey();
                String body = buildEmailBody(entry.getValue());

                logger.info("Sending email to {}", email);


                //todo learn and complete this emailService
                try {
                    emailService.sendEmail(email, "Reminder: You Have Events to attend", body);
                    logger.info("sucessfully sent email  remainder for upcoming event");
                } catch (Exception e) {
                    logger.info("failed to send email remainder to :" + email);
                }
            }
//            for (UserTicket userTicket : userEventsForRemainder) {
//
//                userTicket.setRemainderSent(true);
//                userTicketRepository.save(userTicket);
//            }
            for(UserTicketDTO ticketDTO:userTomorrowEventsForRemainder){
            UserTicket userTicket =   ticketDTO.getUserTicket();
            userTicket.setRemainderSent(true);
            userTicketRepository.save(userTicket);
               }

        } catch (Exception e) {
            logger.error("Error fetching events for reminder", e);
        }
        logger.info("ReminderScheduler finished running at {}", LocalDateTime.now());
    }

    private String buildEmailBody(List<UserTicketDTO> value) {
        if (value.isEmpty()) {
            return "";
        }
        String newLine = "<br>";
        String body = " you have below upcoming events to attend"+newLine;
        int i = 1;
//        for (UserTicket userTicket : value) {
//            body = body + "Event " + i + newLine;
//            body = body + ("Event Name:" + userTicket.getEvent().getName() + newLine);
//            body = body + (("performer:" + userTicket.getEvent().getPerformer() + newLine));
//            body += ("city:" + userTicket.getEvent().getCity() + newLine);
//            body += ("eventStartDate:" + userTicket.getEvent().getEventStartTime() + newLine);
//
//            Integer ticketID = userTicket.getId();
//            String ticketNo = String.valueOf(ticketID);
//            body += ("TicketId" + ticketNo + newLine);
//
//            Integer seats = userTicket.getSeatsBooked();
//            String seatsBooked = String.valueOf(seats);
//            body += ("SeatsBooked:" + seatsBooked + newLine);
//            i++;
//        }

                for (UserTicketDTO userTicketDTO : value) {
            body = body + "Event " + i + newLine;
            body = body + ("Event Name:" + userTicketDTO.getEventName() + newLine);
            body = body + (("performer:" + userTicketDTO.getPerformer() + newLine));
            body += ("city:" + userTicketDTO.getCity() + newLine);
            body += ("eventStartDate:" + userTicketDTO.getEventStartTime() + newLine);
            body +=("TicketId" + userTicketDTO.getUserTicket().getId() + newLine);
            body += ("SeatsBooked:" + userTicketDTO.getUserTicket().getSeatsBooked() + newLine);

            i++;
        }

        System.out.println(body);
        return body;
    }
}

