package com.walmart.ticketservice.controller;

import com.walmart.ticketservice.StrategyFactory;
import com.walmart.ticketservice.bookingstrategy.BookingStrategy;
import com.walmart.ticketservice.config.TicketServiceConfig;
import com.walmart.ticketservice.model.SeatToBeBooked;
import com.walmart.ticketservice.model.Ticket;
import com.walmart.ticketservice.model.dto.BookDTO;
import com.walmart.ticketservice.model.dto.BookingRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ticketservice")
@Api(value = "HoldReserveBuyTicket")
public class BookingController {

  private static final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

  private TicketServiceConfig ticketServiceConfig;
  private BookingStrategy bookingService;

  private StrategyFactory strategyFactory;

  public BookingController(
      StrategyFactory strategyFactory, TicketServiceConfig ticketServiceConfig) {

    Optional<BookingStrategy> bookingService =
        strategyFactory.getBookingStrategy(ticketServiceConfig.getBookingStrategyDefined());
    if (!bookingService.isPresent()) throw new RuntimeException("no booking configuration found");
    this.bookingService = bookingService.get();
  }

  @GetMapping(value = "/availables/{venueId}/{stageId}/{showId}")
  @ApiOperation(
    value =
        "Find all seats available given a venue id, stage and seat id. So seats not held/reserved or commited",
    notes = "We use this list as input in findBestAndHeld"
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "List of seats to be held"),
    @ApiResponse(code = 404, message = "Not available list")
  })
  public ResponseEntity<List<SeatToBeBooked>> findAvailables(
      @NotNull @PathVariable String venueId,
      @NotNull @PathVariable String stageId,
      @NotNull @PathVariable String showId) {

    Optional<List<SeatToBeBooked>> response =
        bookingService.findAvailables(venueId, stageId, showId);
    LOGGER.info(" response " + response.isPresent());

    if (!response.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    LOGGER.info(" response size" + response.get().size());

    return new ResponseEntity<>(response.get(), HttpStatus.OK);
  }

  @PostMapping(
    value = "/booking/hold/",
    consumes = {MediaType.APPLICATION_JSON_VALUE}
  )
  @ApiOperation(
    value =
        "Find all seats available given a venue id, stage and seat id and then held the best ones",
    notes = ""
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "List of best seats held per customer id"),
    @ApiResponse(code = 404, message = "Not available list")
  })
  public ResponseEntity<List<SeatToBeBooked>> findBestAndHeld(
      @RequestBody @Valid @NotNull BookingRequest bookingRequest) {
    LOGGER.info(
        "trying to book  seats:{}  for  customer:{} ",
        bookingRequest.getSeatsToHeld(),
        bookingRequest.getCustomerId());
    if (isThereAnyBlank(bookingRequest)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    Optional<List<SeatToBeBooked>> seats = bookingService.findAndHeld(bookingRequest);
    if (!seats.isPresent()) return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    return new ResponseEntity<>(seats.get(), HttpStatus.OK);
  }

  @PostMapping("/booking/reservation/")
  @ApiOperation(value = "Reserve all seats held  given a venue id, stage and seat id.", notes = "")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Reserved all seats"),
    @ApiResponse(code = 404, message = "Not available list")
  })
  public ResponseEntity<List<BookDTO>> reserve(
      @RequestBody @Valid @NotNull BookingRequest bookingRequest) {
    if (isThereAnyBlank(bookingRequest) || StringUtils.isBlank(bookingRequest.getCustomerId()))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    List<BookDTO> reservedSeats = bookingService.reserve(bookingRequest);
    return new ResponseEntity<>(reservedSeats, HttpStatus.OK);
  }

  @PostMapping("/booking/commited/")
  @ApiOperation(
    value = "Commit all seats reserved given a venue id, stage and seat id.",
    notes = ""
  )
  @ApiResponses({
    @ApiResponse(code = 200, message = "Reserved all seats"),
    @ApiResponse(code = 404, message = "Not available list")
  })
  public ResponseEntity<List<Ticket>> commit(
      @RequestBody @Valid @NotNull BookingRequest bookingRequest) {
    if (isThereAnyBlank(bookingRequest) || StringUtils.isBlank(bookingRequest.getCustomerId()))
      return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

    List<Ticket> tickets = bookingService.commit(bookingRequest);
    return new ResponseEntity<>(tickets, HttpStatus.OK);
  }

  /**
   * Validate the {@link BookingRequest}. It validates that there are not empty values.
   *
   * @param bookingRequest
   * @return
   */
  private boolean isThereAnyBlank(BookingRequest bookingRequest) {
    LOGGER.info(
        "is there any blank "
            + (StringUtils.isBlank(bookingRequest.getShowId())
                || StringUtils.isBlank(bookingRequest.getStage())
                || StringUtils.isBlank(bookingRequest.getVenueId())));

    return StringUtils.isBlank(bookingRequest.getShowId())
        || StringUtils.isBlank(bookingRequest.getStage())
        || StringUtils.isBlank(bookingRequest.getVenueId());
  }
}
