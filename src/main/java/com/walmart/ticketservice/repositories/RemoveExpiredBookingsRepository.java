package com.walmart.ticketservice.repositories;

import com.walmart.ticketservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RemoveExpiredBookingsRepository extends JpaRepository<Booking, Long> {

  @Query(
      "SELECT b FROM Booking b WHERE  b.expirationTime is not null and b.expirationTime<:expiration_time")
  public List<Booking> findExpiredReservations(
      @Param("expiration_time") LocalDateTime expiration_time);
}
