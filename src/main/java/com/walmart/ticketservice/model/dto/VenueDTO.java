package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class VenueDTO implements Serializable {
  static final long serialVersionUID = 1L;

  private String venueId;
  private String name;
  private List<StageDTO> stages;
  private AddressDTO address;
}
