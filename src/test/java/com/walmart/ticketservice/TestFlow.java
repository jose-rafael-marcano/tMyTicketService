package com.walmart.ticketservice;

import com.walmart.ticketservice.config.TicketServiceConfig;
import com.walmart.ticketservice.healthcheck.VenueHealthCheck;
import com.walmart.ticketservice.model.SeatToBeBooked;
import com.walmart.ticketservice.model.Show;
import com.walmart.ticketservice.model.ShowStatus;
import com.walmart.ticketservice.model.dto.*;
import org.assertj.core.api.SoftAssertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(
  classes = DemoApplication.class,
  webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
public class TestFlow {

  private static final Logger LOGGER = LoggerFactory.getLogger(VenueHealthCheck.class);

  private RestTemplate restTemplate;

  @LocalServerPort private int port;

  @Autowired private TicketServiceConfig ticketServiceConfig;

  @Before
  public void before() {}

  @Test
  public void createHold() {

    VenueResponseDTO venueResponseDTO = createVenue();

    Show show = createShowSecondStep(venueResponseDTO);

    String customerId = createCustomerThirdStep();

    BookingRequest bookingRequest =
        createRequestForBooking(show.getVenueId(), show.getStageId(), show.getId(), customerId);

    // hold 6 seats
    List<SeatToBeBooked> seatsHeld = heldSeats(bookingRequest);

    System.out.println("seatsHeld=" + seatsHeld.size());
    // reserve it
    List<BookDTO> seatsReserved = reserveSeats(bookingRequest);
    System.out.println("seatsReserved=" + seatsReserved.size());

    // buy the 6 seats
    List<SeatToBeBooked> seatCommited = commitSeats(bookingRequest);
    System.out.println("seatCommited=" + seatCommited.size());
  }

  /** @param bookingRequest */
  private List<SeatToBeBooked> commitSeats(BookingRequest bookingRequest) {
    restTemplate = new RestTemplate();

    HttpEntity<BookingRequest> request = new HttpEntity<>(bookingRequest);

    ResponseEntity<List<SeatToBeBooked>> response =
        restTemplate.exchange(
            createURLWithPort("/ticketservice/booking/commited/"),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<SeatToBeBooked>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SeatToBeBooked> seats = response.getBody();
    assertThat(seats).isNotEmpty().hasSize(6);

    return seats;
  }

  private List<BookDTO> reserveSeats(BookingRequest bookingRequest) {
    restTemplate = new RestTemplate();

    HttpEntity<BookingRequest> request = new HttpEntity<>(bookingRequest);

    ResponseEntity<List<BookDTO>> response =
        restTemplate.exchange(
            createURLWithPort("/ticketservice/booking/reservation/"),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<BookDTO>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<BookDTO> seats = response.getBody();
    assertThat(seats).isNotEmpty().hasSize(6);
    return seats;
  }

  private List<SeatToBeBooked> heldSeats(BookingRequest bookingRequest) {
    restTemplate = new RestTemplate();

    HttpEntity<BookingRequest> request = new HttpEntity<>(bookingRequest);

    ResponseEntity<List<SeatToBeBooked>> response =
        restTemplate.exchange(
            createURLWithPort("/ticketservice/booking/hold/"),
            HttpMethod.POST,
            request,
            new ParameterizedTypeReference<List<SeatToBeBooked>>() {});

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

    List<SeatToBeBooked> seats = response.getBody();

    assertThat(seats).isNotEmpty().hasSize(6);
    return seats;
  }

  private BookingRequest createRequestForBooking(
      String venueId, String stageId, String showId, String customerId) {
    BookingRequest bookingRequest = new BookingRequest();
    bookingRequest.setVenueId(venueId);
    bookingRequest.setShowId(showId);
    bookingRequest.setCustomerId(customerId);
    bookingRequest.setContiguousSeat(false);
    bookingRequest.setSeatsToHeld(6);
    bookingRequest.setStage(stageId);
    return bookingRequest;
  }

  private String createCustomerThirdStep() {
    String customerId = "";
    restTemplate = new RestTemplate();

    CustomerRequest customerRequest = createCustomerRequest();

    HttpEntity<CustomerRequest> request = new HttpEntity<>(customerRequest);

    ResponseEntity<String> response =
        restTemplate.exchange(
            createURLWithPort("/ticketservice/customer"), HttpMethod.POST, request, String.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    customerId = response.getBody();
    assertThat(customerId).isNotEmpty();
    System.out.println("customerId=" + customerId);
    return customerId;
  }

  private CustomerRequest createCustomerRequest() {
    CustomerRequest customerRequest = new CustomerRequest();
    AddressDTO address = new AddressDTO();
    address.setCity("city");
    address.setState("CA");
    address.setStreet("street");
    address.setZipCode("zipCode");
    customerRequest.setAddress(address);
    customerRequest.setDateOfBirth(new Date());
    customerRequest.setLastFourOfCard(1234);
    customerRequest.setName("Joe");
    customerRequest.setEmail("jose.rafael.marcano.r@gmail.com");
    return customerRequest;
  }

  public VenueResponseDTO createVenue() {
    VenueResponseDTO venueResp = null;
    try {
      LOGGER.info("Create venue");
      VenueRequest venue = createVenueRequest();

      HttpEntity<VenueRequest> request = new HttpEntity<>(venue);
      String venueResourceUrl = ticketServiceConfig.getVenueURL();
      LOGGER.info("url =" + venueResourceUrl);

      restTemplate = new RestTemplate();
      ResponseEntity<VenueResponseDTO> response =
          restTemplate.exchange(
              createURLWithPort("/ticketservice/venue"),
              HttpMethod.POST,
              request,
              VenueResponseDTO.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      venueResp = response.getBody();
      SoftAssertions softly = new SoftAssertions();
      assertThat(venueResp.getStageDTOS()).isNotEmpty();
      assertThat(venueResp.getName()).isNotBlank().isEqualTo("venueVenueHealthCheck");
      assertThat(venueResp.getStageDTOS()).isNotEmpty().hasSize(1);
      assertThat(venueResp.getStageDTOS().get(0))
          .hasFieldOrPropertyWithValue("name", "stageVenueHealthCheck");
      softly.assertAll();

    } catch (Throwable e) {
      LOGGER.error("error calling venue service", e);
      throw e;
    }

    //    try {
    //      Thread.sleep(100000);
    //    } catch (InterruptedException e) {
    //      e.printStackTrace();
    //    }
    return venueResp;
  }

  private Show createShowSecondStep(VenueResponseDTO venueResp) {
    restTemplate = new RestTemplate();

    Show showRequest = createShowDTO(venueResp);

    HttpEntity<Show> request = new HttpEntity<>(showRequest);

    ResponseEntity<Show> showResponse =
        restTemplate.exchange(
            createURLWithPort("/ticketservice/show"), HttpMethod.POST, request, Show.class);

    assertThat(showResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);

    Show showRes = showResponse.getBody();

    assertThat(showRes.getId()).isNotEmpty();
    System.out.println("showRes.getId()=" + showRes.getId());

    return showRes;
  }

  private Show createShowDTO(VenueResponseDTO venueResp) {
    Show show = new Show();
    show.setArtist("Shakira");
    show.setDuration(120);
    show.setName("Chaquira");
    show.setPriceTickets(new BigDecimal(100));
    System.out.println(
        "venueResp.getStageDTOS().get(0).getStageId()="
            + venueResp.getStageDTOS().get(0).getStageId());
    show.setStageId(venueResp.getStageDTOS().get(0).getStageId());
    show.setTime(LocalDateTime.now());
    System.out.println("venueResp.getVenueId()=" + venueResp.getVenueId());
    show.setVenueId(venueResp.getVenueId());
    show.setStatus(ShowStatus.Opened);
    return show;
  }

  private VenueRequest createVenueRequest() {
    VenueRequest venue = new VenueRequest();
    AddressDTO address = createAddres();
    venue.setAddress(address);
    venue.setName("venueVenueHealthCheck");
    List<StageRequest> stages = createStages();
    venue.setStages(stages);

    return venue;
  }

  /**
   * *
   *
   * @return
   */
  private List<StageRequest> createStages() {
    List<StageRequest> stages = new ArrayList<>();
    StageRequest stage = new StageRequest();
    List<FloorRequest> floors = createFloors();
    stage.setFloors(floors);
    stage.setName("stageVenueHealthCheck");
    stages.add(stage);
    return stages;
  }

  /** @return */
  private List<FloorRequest> createFloors() {
    List<FloorRequest> floors = new ArrayList<>();
    FloorRequest floor = new FloorRequest();
    floor.setCols(33);
    floor.setRows(9);
    floor.setFloor(1);
    floors.add(floor);
    return floors;
  }

  private AddressDTO createAddres() {
    AddressDTO address = new AddressDTO();
    address.setCity("cityVenueHealthCheck");
    address.setState("stateVenueHealthCheck");
    address.setZipCode("92129VenueHealthCheck");
    address.setStreet("streetVenueHealthCheck");
    return address;
  }

  private String createURLWithPort(String uri) {
    return "http://localhost:" + port + uri;
  }
}
