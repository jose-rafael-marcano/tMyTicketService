package com.walmart.ticketservice.bookingstrategy;

import com.walmart.ticketservice.model.Booking;
import com.walmart.ticketservice.model.SeatToBeBooked;
import com.walmart.ticketservice.model.Ticket;
import com.walmart.ticketservice.model.dto.BookDTO;
import com.walmart.ticketservice.model.dto.BookingRequest;

import java.util.List;
import java.util.Optional;

/** */
public interface BookingStrategy {
  public Optional<List<SeatToBeBooked>> findAvailables(String venueId, String stage, String showId);

  Optional<List<SeatToBeBooked>> findAndHeld(BookingRequest bookingRequest);

  List<BookDTO> reserve(BookingRequest bookingRequest);

  public List<Ticket> commit(BookingRequest bookingRequest);

  boolean updateBooking(Booking seatToBeBooked);
}
