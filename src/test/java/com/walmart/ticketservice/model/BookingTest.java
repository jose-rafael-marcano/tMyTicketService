package com.walmart.ticketservice.model;

import org.assertj.core.api.SoftAssertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class BookingTest {
  @Test
  public void testBookingEntity() {
    Booking booking = new Booking();
    booking.setStatus(SeatStatus.Reserved);
    LocalDateTime ldtNow = LocalDateTime.now();
    booking.setExpirationTime(ldtNow);
    booking.setCustomerId("custId");
    booking.setCol(1);
    booking.setFloor(1);
    BigDecimal bigDecimal = new BigDecimal(100);
    booking.setPrice(bigDecimal);
    booking.setRow(1);
    booking.setSeatNumber(1);
    booking.setShowId("showId");
    booking.setStageId("stageId");

    SoftAssertions softly = new SoftAssertions();

    assertThat(booking)
        .hasFieldOrPropertyWithValue("showId", "showId")
        .hasFieldOrPropertyWithValue("stageId", "stageId");

    assertThat(booking)
        .hasFieldOrPropertyWithValue("row", 1)
        .hasFieldOrPropertyWithValue("col", 1)
        .hasFieldOrPropertyWithValue("seatNumber", 1);

    assertThat(booking.getPrice()).isEqualTo(bigDecimal);

    assertThat(booking.getExpirationTime()).isBefore(LocalDateTime.now()).isEqualTo(ldtNow);

    softly.assertAll();
  }
}
