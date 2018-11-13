package com.walmart.ticketservice;

import com.walmart.ticketservice.bookingstrategy.BookingServicePerSeatNumber;
import com.walmart.ticketservice.bookingstrategy.BookingStrategy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class StrategyFactory {
  private static final String BEST_AVAILABLE_BASED_ON_ROW_SEAT_NUMBER =
      "BestAvailableBasedOnRowSeatNumber";
  private BookingStrategy bookingStrategy;

  private BookingServicePerSeatNumber bookingServicePerSeatNumber;

  private StrategyFactory(BookingServicePerSeatNumber bookingServicePerSeatNumber) {
    this.bookingServicePerSeatNumber = bookingServicePerSeatNumber;
  }

  public Optional<BookingStrategy> getBookingStrategy(String bookingStrategy) {
    if (BEST_AVAILABLE_BASED_ON_ROW_SEAT_NUMBER.equals(bookingStrategy))
      return Optional.of(bookingServicePerSeatNumber);

    return Optional.empty();
  }
}
