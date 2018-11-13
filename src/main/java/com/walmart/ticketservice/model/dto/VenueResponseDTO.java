package com.walmart.ticketservice.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class VenueResponseDTO implements Serializable {
  static final long serialVersionUID = 1L;

  List<StageDTO> stageDTOS;
  private String venueId;
  private String name;
}
