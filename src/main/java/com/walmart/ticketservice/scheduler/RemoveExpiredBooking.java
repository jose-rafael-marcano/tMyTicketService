package com.walmart.ticketservice.scheduler;

import com.walmart.ticketservice.model.Booking;
import com.walmart.ticketservice.repositories.RemoveExpiredBookingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class RemoveExpiredBooking {

  private static final Logger LOGGER = LoggerFactory.getLogger(RemoveExpiredBooking.class);
  private RemoveExpiredBookingsRepository removeExpiredBookingsRepository;

  public RemoveExpiredBooking(RemoveExpiredBookingsRepository removeExpiredBookingsRepository) {
    this.removeExpiredBookingsRepository = removeExpiredBookingsRepository;
  }

  @Scheduled(fixedRate = 1000 * 60)
  public void removeExpiredReservations() {
    LocalDateTime now = LocalDateTime.now();
    LOGGER.info("The time is now {}", now);
    List<Booking> bookingToBeRemoved = removeExpiredBookingsRepository.findExpiredReservations(now);
    LOGGER.info("The bookingToBeRemoved is {}: now {}", bookingToBeRemoved.size(), now);
    bookingToBeRemoved.forEach(
        booking -> {
          booking.setCustomerId(null);
          booking.setExpirationTime(null);
          booking.setStatus(null);
        });
//    for (Booking booking : bookingToBeRemoved) {
//      booking.setCustomerId(null);
//      booking.setExpirationTime(null);
//      booking.setStatus(null);
//    }
    // removeExpiredBookingsRepository.deleteInBatch(bookingToBeRemoved);
    removeExpiredBookingsRepository.saveAll(bookingToBeRemoved);
    LOGGER.info("Updated expired seats{}", now);
  }
}
