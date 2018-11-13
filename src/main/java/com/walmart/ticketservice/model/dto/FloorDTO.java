package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class FloorDTO implements Serializable {
  static final long serialVersionUID = 1L;
  private String florId;
  private int floor;
  private int cols;
  private int rows;
}
