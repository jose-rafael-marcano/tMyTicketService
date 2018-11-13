package com.walmart.ticketservice.service;

import com.walmart.ticketservice.model.Booking;
import com.walmart.ticketservice.model.Show;
import com.walmart.ticketservice.model.venue.Floor;
import com.walmart.ticketservice.model.venue.Stage;
import com.walmart.ticketservice.repositories.BookingRepository;
import com.walmart.ticketservice.repositories.ShowRepository;
import com.walmart.ticketservice.repositories.StageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ShowService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShowService.class);

  private ShowRepository showRepository;
  private StageFactory stageFactory;
  private BookingRepository bookingRepository;

  public ShowService(
      ShowRepository showRepository,
      StageFactory stageFactory,
      BookingRepository bookingRepository) {
    this.showRepository = showRepository;
    this.stageFactory = stageFactory;
    this.bookingRepository = bookingRepository;
  }

  /** @param show */
  @Transactional
  public Optional<Show> createShow(Show show) {
    showRepository.save(show);

    // get Stage and floors with row cols and assuming cols are the same per row.
    Stage stage = stageFactory.findByStageIdAndAndVenue(show.getStageId(), show.getVenueId());

    if (stage == null) {
      LOGGER.error(
          "invalid show.getStageId =" + show.getStageId() + " venue id =" + show.getVenueId());
      throw new RuntimeException(
          "invalid show.getStageId =" + show.getStageId() + " venue id =" + show.getVenueId());
    }

    createBooking(stage, show);

    return Optional.of(show);
  }

  @Transactional
  public Optional<String> deleteVenue(String showId) {
    Show currentVenue = showRepository.getOne(showId);
    if (currentVenue == null) return Optional.empty();
    showRepository.deleteById(showId);
    LOGGER.info("Deleted Show with Id:{}", showId);
    return Optional.of(showId);
  }

  // @Transactional
  protected void createBooking(Stage stage, Show show) {
    List<Floor> floors = stage.getFloors();
    int seatNumber = 1;
    int row = 1;
    int col = 1;
    int seatsPerFlow = 0;
    int i = 0;
    List<Booking> bookings = new ArrayList<>();
    for (Floor floor : floors) { // O(1) because not max than 10 floors.
      seatsPerFlow = floor.getCols() * floor.getRows();
      // O(seatsPerFlow) //max seats in stadium 200000 for normal theaters 200 to 2000
      while (i < seatsPerFlow) {
        Booking booking = new Booking();
        if (col == floor.getCols()) {
          booking.setCol(col);
          col = 1;
          booking.setRow(row++);
        } else {
          booking.setRow(row);
          booking.setCol(col++);
        }
        booking.setSeatNumber(seatNumber++);
        booking.setPrice(show.getPriceTickets()); // asuming same price per ticket.
        booking.setShowId(show.getId());
        booking.setStageId(stage.getStageId());
        booking.setShowTime(show.getTime());
        booking.setFloor(floor.getFloor());
        booking.setVenueId(show.getVenueId());
        // bookingRepository.save(booking);
        bookings.add(booking);
        i++;
      }
      i = 0;
    }
    bookingRepository.saveAll(bookings);
  }

  public Optional<List<Show>> getShows(String showId) {
    List<Show> response = showRepository.findByShowId(showId);
    return Optional.ofNullable(response);
  }
}
