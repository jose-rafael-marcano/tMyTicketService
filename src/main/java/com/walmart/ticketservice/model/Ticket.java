package com.walmart.ticketservice.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
/** We can use this class for history */
@Entity
public class Ticket implements Serializable {

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id")
  private String id;

  private String showId; // reference id to show
  private int seatNumber; // duplicate to avoid join to seat table
  private int col; // duplicate to avoid join to seat table due to perfomance issues during joins
  private int row; // duplicate to avoid join to seat table
  private LocalDateTime showTime;
  private String costumerId;
  private String venue;
  private BigDecimal price;
  private LocalTime timeOfPurchase;
  @Version private long version;
}
