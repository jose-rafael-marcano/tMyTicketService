package com.walmart.ticketservice.bookingstrategy;

import com.walmart.ticketservice.config.TicketServiceConfig;
import com.walmart.ticketservice.model.Booking;
import com.walmart.ticketservice.model.SeatStatus;
import com.walmart.ticketservice.model.SeatToBeBooked;
import com.walmart.ticketservice.model.Ticket;
import com.walmart.ticketservice.model.dto.BookDTO;
import com.walmart.ticketservice.model.dto.BookingRequest;
import com.walmart.ticketservice.repositories.BookingRepository;
import com.walmart.ticketservice.repositories.TicketRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BookingServicePerSeatNumber implements BookingStrategy {

  private static final Logger LOGGER = LoggerFactory.getLogger(BookingServicePerSeatNumber.class);
  private static final int MAX_SEAT_TO_BOOK = 20; // TODO: put this in configuration
  private BookingRepository bookingRepository;
  private TicketRepository ticketRepository;
  private TicketServiceConfig ticketServiceConfig;
  private long expirationTimeForHeldTickes = 60; // TODO: put this in configuration
  private long expirationTimeForReservedTickes = 600; // TODO: put this in configuration

  /**
   * @param bookingRepository
   * @param ticketRepository
   */
  public BookingServicePerSeatNumber(
      BookingRepository bookingRepository,
      TicketRepository ticketRepository,
      TicketServiceConfig ticketServiceConfig) {
    this.bookingRepository = bookingRepository;
    this.ticketRepository = ticketRepository;
    this.ticketServiceConfig = ticketServiceConfig;
  }

  /**
   * @param venueId
   * @param stageId
   * @param showId
   * @return
   */
  @Override
  public Optional<List<SeatToBeBooked>> findAvailables(
      String venueId, String stageId, String showId) {
    List<Booking> availables = bookingRepository.findAvailables(showId, venueId, stageId);
    if (availables != null && availables.size() == 0) return Optional.empty();

    LOGGER.info("Seats availabales:{}", availables.size());
    List<SeatToBeBooked> seatToBeBookeds = new ArrayList<>();
    for (Booking booking : availables) {
      SeatToBeBooked seatToBeBooked = new SeatToBeBooked();
      seatToBeBooked.setSeatNumber(booking.getSeatNumber());
      seatToBeBooked.setRow(booking.getRow());
      seatToBeBooked.setCol(booking.getCol());
      seatToBeBooked.setPrice(booking.getPrice());
      seatToBeBookeds.add(seatToBeBooked);
    }
    LOGGER.info("size of List of seats to be booked=" + seatToBeBookeds.size());
    return Optional.of(seatToBeBookeds);
  }

  /**
   * @param bookingRequest
   * @return
   */
  @Override
  public List<BookDTO> reserve(BookingRequest bookingRequest) {
    System.out.println("SeatStatus.Held=" + SeatStatus.Held);
    List<Booking> availables =
        bookingRepository.findByCustomerIdAndShowIdAAndStatus(
            bookingRequest.getShowId(),
            bookingRequest.getVenueId(),
            bookingRequest.getStage(),
            bookingRequest.getCustomerId(),
            SeatStatus.Held);
    if (availables == null || availables.size() == 0)
      throw new RuntimeException("Not found tickets or already expired");
    List<BookDTO> reserved = new ArrayList<>();
    for (Booking booking : availables) {
      booking.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTimeForReservedTickes));
      booking.setStatus(SeatStatus.Reserved);
      boolean booked = updateBooking(booking);
      if (booked) {
        reserved.add(createBookDTO(booking));
      }
    }
    return reserved;
  }

  /**
   * @param booking
   * @return
   */
  private BookDTO createBookDTO(Booking booking) {
    BookDTO bookDTO = new BookDTO();
    bookDTO.setCol(booking.getCol());
    bookDTO.setFloor(booking.getFloor());
    bookDTO.setPrice(booking.getPrice());
    bookDTO.setRow(booking.getRow());
    bookDTO.setSeatNumber(booking.getSeatNumber());
    bookDTO.setShowId(booking.getShowId());
    bookDTO.setShowTime(booking.getShowTime());
    return bookDTO;
  }

  @Transactional
  @Override
  public List<Ticket> commit(BookingRequest bookingRequest) {
    System.out.println("SeatStatus.Reserved=" + SeatStatus.Reserved);
    List<Booking> availables =
        bookingRepository.findByCustomerIdAndShowIdAAndStatus(
            bookingRequest.getShowId(),
            bookingRequest.getVenueId(),
            bookingRequest.getStage(),
            bookingRequest.getCustomerId(),
            SeatStatus.Reserved);
    if (availables == null || availables.size() == 0)
      throw new RuntimeException("Not found tickets or already expired");

    List<Ticket> purchase = new ArrayList<>();
    for (Booking booking : availables) {
      booking.setExpirationTime(LocalDateTime.now().plusSeconds(expirationTimeForReservedTickes));
      booking.setStatus(SeatStatus.Booked);
      Ticket ticket = createTicket(booking);
      ticketRepository.save(ticket);
      bookingRepository.delete(booking);
      purchase.add(ticket);
    }
    return purchase;
  }

  /**
   * @param booking
   * @return
   */
  private Ticket createTicket(Booking booking) {
    Ticket ticket = new Ticket();
    ticket.setCol(booking.getCol());
    ticket.setCostumerId(booking.getCustomerId());
    ticket.setPrice(booking.getPrice());
    ticket.setRow(booking.getRow());
    ticket.setSeatNumber(booking.getSeatNumber());
    ticket.setShowId(booking.getShowId());
    ticket.setShowTime(booking.getShowTime());
    ticket.setTimeOfPurchase(LocalTime.now());
    ticket.setVenue(booking.getVenueId());
    return ticket;
  }

  /**
   * @param bookingRequest
   * @return
   */
  @Override
  public Optional<List<SeatToBeBooked>> findAndHeld(BookingRequest bookingRequest) {
    int seatsToHeld = bookingRequest.getSeatsToHeld();
    if (seatsToHeld > ticketServiceConfig.getMaxSeatsToBook())
      throw new RuntimeException(
          "you cannot reserve more than " + ticketServiceConfig.getMaxSeatsToBook());

    // seatAvailable sorted per seatId
    List<Booking> seatAvailables =
        bookingRepository.findAvailables(
            bookingRequest.getShowId(), bookingRequest.getVenueId(), bookingRequest.getStage());
    if (seatAvailables.size() < bookingRequest.getSeatsToHeld() || seatAvailables.size() == 0)
      return Optional.empty();

    int i = 0;
    List<SeatToBeBooked> held = new ArrayList<>();
    Booking previousSeat = null;
    while (seatsToHeld > 0 && i < seatAvailables.size()) {
      Booking seatToBeBooked = seatAvailables.get(i);
      if (bookingRequest.getContiguousSeat()) {
        if (i >= 1 && validateContiguosSeat(seatToBeBooked, previousSeat)) {
          LOGGER.warn("not contiguous seats available ");
          return Optional.of(held);
        }
        previousSeat = seatToBeBooked;
      }
      seatToBeBooked.setExpirationTime(
          LocalDateTime.now().plusSeconds(expirationTimeForHeldTickes));
      seatToBeBooked.setStatus(SeatStatus.Held);
      seatToBeBooked.setCustomerId(bookingRequest.getCustomerId());
      if (updateBooking(seatToBeBooked)) {
        seatsToHeld -= 1;
        SeatToBeBooked seatHeld = createSeatToBeBooked(seatToBeBooked);
        held.add(seatHeld);
      }
      i++;
    }
    return Optional.of(held);
  }

  /**
   * For performance only put transaction around one update. And because we can get another seat for
   * not continuous case
   */
  @Transactional
  @Override
  public boolean updateBooking(Booking seatToBeBooked) {
    try {
      bookingRepository.save(seatToBeBooked);
    } catch (JpaOptimisticLockingFailureException e) {
      LOGGER.warn("Seat" + seatToBeBooked.getSeatNumber() + "Already booked ", e);
      return false;
    }
    return true;
  }

  /**
   * @param seatToBeBooked
   * @param previousSeat
   * @return
   */
  private boolean validateContiguosSeat(Booking seatToBeBooked, Booking previousSeat) {
    return (seatToBeBooked.getSeatNumber() - previousSeat.getSeatNumber() > 1)
        || (seatToBeBooked.getRow() != previousSeat.getRow());
  }

  /**
   * @param seatToBeBooked
   * @return
   */
  private SeatToBeBooked createSeatToBeBooked(Booking seatToBeBooked) {
    SeatToBeBooked seatHeld = new SeatToBeBooked();
    seatHeld.setCol(seatToBeBooked.getCol());
    seatHeld.setRow(seatToBeBooked.getRow());
    seatHeld.setPrice(seatToBeBooked.getPrice());
    seatHeld.setSeatNumber(seatToBeBooked.getSeatNumber());
    return seatHeld;
  }
}
