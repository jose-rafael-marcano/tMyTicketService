package com.walmart.ticketservice.controller;

import com.walmart.ticketservice.model.dto.VenueDTO;
import com.walmart.ticketservice.model.dto.VenueRequest;
import com.walmart.ticketservice.model.dto.VenueResponseDTO;
import com.walmart.ticketservice.model.venue.Venue;
import com.walmart.ticketservice.service.VenueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Optional;

@RestController
@RequestMapping("/ticketservice")
@Api(value = "VenueRepository")
public class VenueController {
  private static final Logger LOGGER = LoggerFactory.getLogger(VenueController.class);
  private VenueService venueService;

  public VenueController(VenueService venueService) {
    this.venueService = venueService;
  }

  @ApiOperation(
    value = "Request to create a Venue location ",
    notes = "The first of three steps in ticketService in order  buy ticket . Returns the Venue ID."
  )
  @ApiResponses({@ApiResponse(code = 201, message = "Venue creation request successful")})
  @PostMapping(value = "/venue")
  public ResponseEntity<VenueResponseDTO> createVenue(
      @RequestBody @Valid @NotNull VenueRequest venueRequest) {
    LOGGER.info("creating venue=" + venueRequest.getName());
    VenueResponseDTO venueResponseDTO = venueService.createVenue(venueRequest);
    LOGGER.info("created venue=" + venueResponseDTO.getVenueId());

    return new ResponseEntity<>(venueResponseDTO, HttpStatus.CREATED);
  }

  @ApiResponses({@ApiResponse(code = 200, message = "get the Venue given the venue id")})
  @GetMapping(value = "/venue/{venueId}")
  public ResponseEntity<VenueDTO> getVenue(@PathVariable @NotNull String venueId) {
    if (StringUtils.isBlank(venueId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    Optional<VenueDTO> venue = venueService.getVenue(venueId);
    if (!venue.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    LOGGER.info("got venue=" + venue.get().getName());
    return new ResponseEntity<>(venue.get(), HttpStatus.OK);
  }

  @ApiResponses({
    @ApiResponse(
      code = 200,
      message = "update the Venue given the venue id. We cannot update the id."
    )
  })
  @PutMapping(value = "/venue")
  public ResponseEntity<Venue> updateVenue(@RequestBody @Valid @NotNull Venue venue) {
    if (StringUtils.isBlank(venue.getVenueId()))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    Optional<Venue> updatedVenue = venueService.updateVenue(venue);
    if (!updatedVenue.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(updatedVenue.get(), HttpStatus.OK);
  }

  @ApiResponses({
    @ApiResponse(
      code = 204,
      message = "Delete the Venue given the venue id. We cannot update the id."
    )
  })
  @DeleteMapping(value = "/venue/{venueId}")
  public ResponseEntity deleteVenue(@PathVariable @Valid @NotNull String venueId) {
    if (StringUtils.isBlank(venueId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    Optional<String> deletedVenue = venueService.deleteVenue(venueId);
    if (!deletedVenue.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    LOGGER.info("Deleted venue=" + venueId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }
}
