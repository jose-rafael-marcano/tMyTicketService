package com.walmart.ticketservice.repositories;

import com.walmart.ticketservice.model.Booking;
import com.walmart.ticketservice.model.SeatStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query(
      "SELECT b FROM Booking b WHERE b.status=null and b.showId =:showId and b.venueId=:venueId and b.stageId=:stageId order by b.seatNumber")
  public List<Booking> findAvailables(
      @Param("showId") String showId,
      @Param("venueId") String venueId,
      @Param("stageId") String stageId);

  @Query(
      "SELECT b FROM Booking b WHERE b.status=:status and b.customerId=:customerId and b.showId =:showId and b.venueId=:venueId and b.stageId=:stageId order by b.seatNumber")
  public List<Booking> findByCustomerIdAndShowIdAAndStatus(
      @Param("showId") String showId,
      @Param("venueId") String venueId,
      @Param("stageId") String stageId,
      @Param("customerId") String customerId,
      @Param("status") SeatStatus status);
}
