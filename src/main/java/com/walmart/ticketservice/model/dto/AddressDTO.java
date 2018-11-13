package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AddressDTO implements Serializable {
  private static final long serialVersionUID = 1L;
  private String id;
  private String street;
  private String state;
  private String city;
  private String zipCode;
}
