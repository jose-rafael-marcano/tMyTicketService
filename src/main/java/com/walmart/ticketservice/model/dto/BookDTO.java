package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
public class BookDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private int seatNumber;
  private String showId;
  private int col; // duplicate to avoid join to seat table due to perfomance issues during joins
  private int row; // duplicate to avoid join to seat table
  private LocalDateTime showTime;
  private BigDecimal price;
  private int floor;
}
