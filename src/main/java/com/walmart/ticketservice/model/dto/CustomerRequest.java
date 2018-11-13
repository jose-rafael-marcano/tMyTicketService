package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class CustomerRequest implements Serializable {
  private static final long serialVersionUID = 1L;
  private String id;
  private String name;
  private int lastFourOfCard;
  private Date dateOfBirth;
  private String email;
  private AddressDTO address;
}
