package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/** */
@Setter
@Getter
public class BookingRequest implements Serializable {
  private static final long serialVersionUID = 1L;

  private String venueId;
  private String stage;
  private String showId;
  private String customerId;
  private int seatsToHeld;
  private Boolean contiguousSeat;
}
