package com.walmart.ticketservice.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class SeatToBeBooked implements Serializable {
  private int seatNumber; // duplicate to avoid join to seat table
  private int floor;
  private int col;
  private int row;
  private int seatId;
  private BigDecimal price;
  private SeatStatus status;
}
