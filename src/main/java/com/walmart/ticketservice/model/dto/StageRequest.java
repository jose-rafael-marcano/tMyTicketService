package com.walmart.ticketservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.walmart.ticketservice.model.venue.Floor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

// @JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class StageRequest implements Serializable {
  static final long serialVersionUID = 1L;

  private List<FloorRequest> floors = new ArrayList<>();
  private String name;
}
