package com.walmart.ticketservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.walmart.ticketservice.model.Address;
import com.walmart.ticketservice.model.venue.Stage;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class VenueRequest implements Serializable {
  static final long serialVersionUID = 1L;
  private String name;
  private List<StageRequest> stages = new ArrayList<>();
  private AddressDTO address;
}
