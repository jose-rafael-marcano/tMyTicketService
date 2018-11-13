package com.walmart.ticketservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
public class FloorRequest implements Serializable {
  static final long serialVersionUID = 1L;
  private int floor;
  private int cols;
  private int rows;
}
