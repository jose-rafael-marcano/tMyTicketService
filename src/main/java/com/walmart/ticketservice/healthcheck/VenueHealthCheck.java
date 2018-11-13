package com.walmart.ticketservice.healthcheck;

import com.walmart.ticketservice.config.TicketServiceConfig;
import com.walmart.ticketservice.model.dto.*;
import com.walmart.ticketservice.service.VenueService;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// import com.walmart.ticketservice.model.dto.;

@Component
public class VenueHealthCheck extends AbstractHealthIndicator {
  private static final Logger LOGGER = LoggerFactory.getLogger(VenueHealthCheck.class);
  private RestTemplate restTemplate = new RestTemplate();
  private TicketServiceConfig ticketServiceConfig;
  //private VenueService venueService;

  /**
   * @param ticketServiceConfig
   * @param venueService
   */
  public VenueHealthCheck(TicketServiceConfig ticketServiceConfig) {
    this.ticketServiceConfig = ticketServiceConfig;
    //this.venueService = venueService;
  }

  @Override
  protected void doHealthCheck(Health.Builder bldr) throws Exception {
    // TODO implement some check
    boolean running = validateVenueService();
    // restTemplate.p
    if (running) {
      bldr.up();
    } else {
      bldr.down();
    }
  }

  private boolean validateVenueService() {
    try {
      LOGGER.info("validating create venue");
      VenueRequest venue = createVenue();

      HttpEntity<VenueRequest> request = new HttpEntity<>(venue);
      String venueResourceUrl = ticketServiceConfig.getVenueURL();
      LOGGER.info("url =" + venueResourceUrl);

      ResponseEntity<VenueResponseDTO> response =
          restTemplate.exchange(venueResourceUrl, HttpMethod.POST, request, VenueResponseDTO.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

      VenueResponseDTO venueResp = response.getBody();
      SoftAssertions softly = new SoftAssertions();
      assertThat(venueResp.getStageDTOS()).isNotEmpty();
      assertThat(venueResp.getName()).isNotBlank().isEqualTo("venueVenueHealthCheck");
      assertThat(venueResp.getStageDTOS()).isNotEmpty().hasSize(1);
      assertThat(venueResp.getStageDTOS().get(0))
          .hasFieldOrPropertyWithValue("name", "stageVenueHealthCheck");
      softly.assertAll();
    } catch (Throwable e) {
      LOGGER.error("error calling venue service", e);
      return false;
    }
    return true;
  }

  private VenueRequest createVenue() {
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
}
