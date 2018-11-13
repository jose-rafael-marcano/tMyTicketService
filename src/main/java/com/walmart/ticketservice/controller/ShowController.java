package com.walmart.ticketservice.controller;

import com.walmart.ticketservice.model.Show;
import com.walmart.ticketservice.service.ShowService;
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
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ticketservice")
@Api("Show information")
public class ShowController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ShowController.class);

  private ShowService showService;

  public ShowController(ShowService showService) {
    this.showService = showService;
  }

  @GetMapping(value = "/show/{venueId}")
  @ApiOperation(
    value = "Find all shows given a venue id",
    notes = "We use this for display all shows availables"
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "List of shows"),
    @ApiResponse(code = 404, message = "Not available shows for the venue")
  })
  public ResponseEntity<List<Show>> getShows(@PathVariable String venueId) {
    if (StringUtils.isBlank(venueId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Optional<List<Show>> shows = showService.getShows(venueId);
    if (!shows.isPresent() || shows.get().size() == 0)
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(shows.get(), HttpStatus.OK);
  }

  @PostMapping("/show")
  @ApiOperation(
    value = "Creat show and populate the booking table",
    notes = "the booking table is used to manage the workflow held->reserve->commit"
  )
  @ApiResponses({
    @ApiResponse(code = 201, message = "Show created"),
    @ApiResponse(code = 404, message = "Not available shows for the venue")
  })
  public ResponseEntity<Show> createShow(@RequestBody @Valid @NotNull Show show) {

    Optional<Show> showCreated = showService.createShow(show);
    if (!showCreated.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(showCreated.get(), HttpStatus.CREATED);
  }

  @PutMapping("/show")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Show created"),
    @ApiResponse(code = 404, message = "Not available shows for the venue")
  })
  public ResponseEntity<Show> updateShow(@RequestBody @Valid @NotNull Show show) {
    Optional<Show> showCreated = showService.createShow(show);
    if (!showCreated.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(showCreated.get(), HttpStatus.CREATED);
  }

  @DeleteMapping("/show/{showId}")
  @ApiResponses({
    @ApiResponse(
      code = 204,
      message = "Delete the Show given the show id. We cannot update the id."
    ),
    @ApiResponse(code = 404, message = "Not available shows for the venue")
  })
  public ResponseEntity deleteShow(@PathVariable String showId) {
    if (StringUtils.isBlank(showId)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    Optional<String> deletedVenue = showService.deleteVenue(showId);
    if (!deletedVenue.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    LOGGER.info("Deleted show=" + showId);
    return new ResponseEntity<>(HttpStatus.NO_CONTENT);
  }

}
