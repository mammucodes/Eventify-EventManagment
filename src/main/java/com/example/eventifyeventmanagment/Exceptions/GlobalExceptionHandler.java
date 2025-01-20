package com.example.eventifyeventmanagment.Exceptions;

import com.example.eventifyeventmanagment.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.error("IllegalArgumentException: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("Validation error: " + ex.getMessage(), "400"), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred test: {}", ex.getMessage());
        return new ResponseEntity<>(new ErrorResponse("An unexpected error occurred test", "500"), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFoundException(EventNotFoundException eve) {
        logger.error("EventNotFoundException:{}", eve.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("Event is not present with id", "400"));
    }

    @ExceptionHandler(InvalidStatusOption.class)
    public ResponseEntity<ErrorResponse> handleInvalidStatusOption(InvalidStatusOption iso) {
        logger.error("InvalidSatusOptionException:{}", iso.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("InvalidStatusOption is passed", "400"));
    }

    @ExceptionHandler(UserFoundIsNotOrganizerException.class)
    public ResponseEntity<ErrorResponse> handleUserFoundIsNotOrganizerException(UserFoundIsNotOrganizerException ufo) {
        logger.error("userFoundIsNotOrganizerException:{}", ufo.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("Organizer Id passed is not valid  organizer ", "400"));
    }

    @ExceptionHandler(EventTicketsOrTicketsPriceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventTicketsOrTicketsPriceNotFoundException(EventTicketsOrTicketsPriceNotFoundException etp) {
        logger.error("Invalid or null  ticektcount or ticketprice passed:{}", etp.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("cannot pass an null ticekt count or ticket price ", "400"));
    }

    @ExceptionHandler(EventTicketsPerUserPassedZeroException.class)
    public ResponseEntity<ErrorResponse> handleEventTicketsPerUserPassedZeroException(EventTicketsPerUserPassedZeroException etz) {
        logger.error("  need to pass ticekts per user :{}", etz.getMessage());
        return ResponseEntity.badRequest().body(new ErrorResponse("cannot pass tickets per user as 0 ", "400"));

    }

    @ExceptionHandler(InsufficientTicketsAvailableException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientTicketsAvailableException(InsufficientTicketsAvailableException ist) {
        logger.error("requested no of tickets  not available");
        return ResponseEntity.badRequest().body(new ErrorResponse("requested no of tickets  not available", "400"));
    }

    @ExceptionHandler(PassedTicketCountIsMoreThanLimitException.class)
    public ResponseEntity<ErrorResponse> handlePassedTicketCountIsMoreThanLimitException(PassedTicketCountIsMoreThanLimitException ptc) {
        logger.error("Passed no of ticket seat   are more than max limit seat count per user. please decrease the count seats to be booked");
        return ResponseEntity.badRequest().body(new ErrorResponse("passed no of seats to be booked is more than max  seat limit count to be booked", "400"));
    }

    @ExceptionHandler(EventTicketDetailsNotFOundException.class)
    public ResponseEntity<ErrorResponse> handleEventTicketDetailsNotFOundException(EventTicketDetailsNotFOundException etd) {
        logger.error("Passed Event tickets details are not found");
        return ResponseEntity.badRequest().body(new ErrorResponse("  no event  tickets  details found Exception", "400"));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException unf) {
        logger.error("Passed User is details are not found in database");
        return ResponseEntity.badRequest().body(new ErrorResponse("passed User Details not found Exception", "400"));
    }

    @ExceptionHandler(UserBookedTicketDetailsNotFounException.class)
    public ResponseEntity<ErrorResponse> handleUserBookedTicketDetailsNotFounException(UserBookedTicketDetailsNotFounException ubt) {
        logger.error("No event tickets details with passed ticketId");
        return ResponseEntity.badRequest().body(new ErrorResponse("Passed TicketId details not found Exception ", "400"));
    }
}