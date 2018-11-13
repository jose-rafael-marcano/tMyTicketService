package com.walmart.ticketservice.model.controllerdto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class VenueResult implements Serializable {
    private String dec;

    private boolean testSuccessful;

    private String venueId;
}
