package com.example.eventifyeventmanagment.controller;

import com.example.eventifyeventmanagment.Exceptions.*;
import com.example.eventifyeventmanagment.dto.request.BookEventTicketRequestDTO;
import com.example.eventifyeventmanagment.dto.response.BookEventTicketResponse;
import com.example.eventifyeventmanagment.dto.response.ErrorResponse;
import com.example.eventifyeventmanagment.dto.response.UserTicketResponseDTO;
import com.example.eventifyeventmanagment.entity.UserTicket;
import com.example.eventifyeventmanagment.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/tickets")
public class TicketController {
    private TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/bookticket/{eventId}")
    public ResponseEntity<?> bookEventTicket(@PathVariable int eventId, @RequestBody BookEventTicketRequestDTO bookEventTicketRequestDTO) throws EventTicketDetailsNotFOundException, EventNotFoundException, InsufficientTicketsAvailableException, UserNotFoundException, PassedTicketCountIsMoreThanLimitException {

        ResponseEntity<ErrorResponse> badRequest = validateBookEventTicket(eventId, bookEventTicketRequestDTO);
        if (badRequest != null) {
            return badRequest;
        }
        UserTicket bookedTicketDetails = ticketService.bookEventTicket(eventId, bookEventTicketRequestDTO);
        BookEventTicketResponse ticketResponse = new BookEventTicketResponse();
        ticketResponse.setTicketId(bookedTicketDetails.getId());
        ticketResponse.setMessage("Sucessfully booked tickets ");
        ticketResponse.setBookedOn(bookedTicketDetails.getTicketBookedOn());
        return ResponseEntity.ok(ticketResponse);

    }

    @GetMapping("/get/{ticketId}")
    public ResponseEntity<?> getUserTicketDetails(@PathVariable Integer ticketId) throws UserBookedTicketDetailsNotFounException, InvalidStatusOption {
        if (ticketId == null || ticketId < 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("passed ticket id is not found in booked ticket details", "400"));
        }

        UserTicketResponseDTO ticketResponseDTO = ticketService.getEventTicketDetails(ticketId);
        System.out.println("WAITING.....");
        try {
            Thread.sleep(2 * 1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DONE!!!!!");
        return ResponseEntity.ok(ticketResponseDTO);

    }

    @PutMapping("/checkincount/{ticketId}")
    public ResponseEntity<?> getCheckIn(@PathVariable Integer ticketId) throws UserBookedTicketDetailsNotFounException, InvalidStatusOption {
        if (ticketId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("evendID cannot be less than or equal to zero", "400"));
        }
        ticketService.getCheckIn(ticketId);
        return ResponseEntity.ok("Sucessfully  updated checkedIn  count of the user");
    }

    @PutMapping("/checkoutcount/{ticketId}")
    public ResponseEntity<?> getCheckOut(@PathVariable Integer ticketId) throws UserBookedTicketDetailsNotFounException {
        if (ticketId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("evendID cannot be less than or equal to zero", "400"));
        }
        ticketService.getCheckOut(ticketId);
        return ResponseEntity.ok("Sucessfully  updated checkOut  count of the user");

    }

    public ResponseEntity<ErrorResponse> validateBookEventTicket(int eventId, BookEventTicketRequestDTO bookEventTicketRequestDTO) {
        if (eventId <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("evendID cannot be less than or equal to zero", "400"));
        }
        if (bookEventTicketRequestDTO == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("object passed is null  cannot book a ticket", "400"));

        }
        if (bookEventTicketRequestDTO.getUserId() == null || bookEventTicketRequestDTO.getUserId() == 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse("userID null or zero passed", "400"));
        }
        if (bookEventTicketRequestDTO.getNoOfSeats() == null || bookEventTicketRequestDTO.getNoOfSeats() <= 0) {
            return ResponseEntity.badRequest().body(new ErrorResponse(" minimum 1 ticket need to be booked ", "400"));
        }
        return null;
    }


}