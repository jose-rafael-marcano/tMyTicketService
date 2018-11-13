package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
public class CustomerResponseDto implements Serializable {
  private static final long serialVersionUID = 1L;

  private String id;
  private String name;
  private int lastFourOfCard;
  private Date dateOfBirth;
  private AddressDTO address;
  private String email;
}
