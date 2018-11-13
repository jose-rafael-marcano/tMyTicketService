package com.walmart.ticketservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
@Setter
public class TicketServiceConfig {
  @Value("${ticketservice.maxSeatsToBook}")
  private int maxSeatsToBook;
  @Value("${ticketservice.expirationTimeForHeldTickes}")
  private int expirationTimeForHeldTickes;
  @Value("${ticketservice.expirationTimeForReservedTickes}")
  private int expirationTimeForReservedTickes;
  @Value("${ticketservice.bookingStrategy}")
  private String bookingStrategyDefined;
  @Value("${ticketservice.venueurl}")
  private String venueURL;

}
