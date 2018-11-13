package com.walmart.ticketservice.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
  name = "booking",
  indexes = {
    @Index(name = "IDX_expiration_time", columnList = "expiration_time"),
    @Index(name = "IDX_SearchPerShowStageVenue", columnList = "SHOW_ID,STAGE_ID,VENUE_ID"),
    @Index(
      name = "IDX_SearchPerShowStageVenueCustomerStatus",
      columnList = "SHOW_ID,STAGE_ID,VENUE_ID,CUSTOMER_ID,STATUS"
    )
  },
  uniqueConstraints = @UniqueConstraint(columnNames = {"SHOW_ID", "seat_Number"})
)
public class Booking implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  private SeatStatus status;

  @NotNull
  @Column(name = "SHOW_ID")
  private String showId; // reference id to show

  @NotNull
  @Column(name = "VENUE_ID")
  private String venueId;

  @NotNull
  @Column(name = "STAGE_ID")
  private String stageId;
  // private int seatId; // reference Id to seat;
  @NotNull
  @Column(name = "seat_Number")
  private int seatNumber; // duplicate to avoid join to seat table

  @NotNull
  private int col; // duplicate to avoid join to seat table due to perfomance issues during joins

  @NotNull private int row; // duplicate to avoid join to seat table
  @NotNull private LocalDateTime showTime;

  @Column(name = "expiration_time")
  private LocalDateTime expirationTime; // for external process to clean up expired bookings

  @Column(name = "CUSTOMER_ID")
  private String customerId;

  private BigDecimal price;

  private int floor;

  @Version private long version;
}
