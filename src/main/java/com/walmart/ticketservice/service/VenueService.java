package com.walmart.ticketservice.service;

import com.walmart.ticketservice.model.Address;
import com.walmart.ticketservice.model.dto.*;
import com.walmart.ticketservice.model.venue.Floor;
import com.walmart.ticketservice.model.venue.Stage;
import com.walmart.ticketservice.model.venue.Venue;
import com.walmart.ticketservice.repositories.VenueRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VenueService {
  private static final Logger LOGGER = LoggerFactory.getLogger(VenueService.class);
  private VenueRepository repository;

  public VenueService(VenueRepository repository) {
    this.repository = repository;
  }

  @Transactional
  public VenueResponseDTO createVenue(VenueRequest venueRequest) {
    Venue venue = mapDtoToEntity(venueRequest);
    Venue venueResponse = repository.save(venue);
    List<StageDTO> stagesDTO = new ArrayList<>();
    for (Stage stage : venueResponse.getStages()) {
      StageDTO stageDTO = new StageDTO();
      stageDTO.setStageId(stage.getStageId());
      stageDTO.setName(stage.getName());
      stagesDTO.add(stageDTO);
    }

    VenueResponseDTO venueResponseDTO = new VenueResponseDTO();
    venueResponseDTO.setVenueId(venueResponse.getVenueId());
    venueResponseDTO.setStageDTOS(stagesDTO);
    venueResponseDTO.setName(venue.getName());
    LOGGER.info("Created Venue:{} with Id:{}", venue.getName(), venue.getVenueId());
    return venueResponseDTO;
  }

  /**
   * @param venueRequest
   * @return
   */
  private Venue mapDtoToEntity(VenueRequest venueRequest) {
    Venue venue = new Venue();
    List<Stage> stages = mapStagesToEntity(venueRequest.getStages(), venue);
    venue.setStages(stages);
    venue.setName(venueRequest.getName());
    Address address = mapAddressDTOToEntity(venueRequest.getAddress());
    address.setVenue(venue);
    venue.setAddress(address);
    return venue;
  }

  /**
   * @param addressDTO
   * @return
   */
  private Address mapAddressDTOToEntity(AddressDTO addressDTO) {
    Address address = new Address();
    address.setStreet(addressDTO.getStreet());
    address.setZipCode(addressDTO.getZipCode());
    address.setState(addressDTO.getState());
    address.setCity(addressDTO.getCity());
    return address;
  }

  /**
   * @param stagesRequest
   * @return
   */
  private List<Stage> mapStagesToEntity(List<StageRequest> stagesRequest, Venue venue) {
    List<Stage> stages = new ArrayList<>();
    for (StageRequest stageRequest : stagesRequest) {
      Stage stage = new Stage();
      stage.setName(stageRequest.getName());
      List<Floor> floors = new ArrayList<>();
      stageRequest
          .getFloors()
          .forEach(
              floorRequest -> {
                Floor floor = new Floor();
                floor.setFloor(floorRequest.getFloor());
                floor.setRows(floorRequest.getRows());
                floor.setCols(floorRequest.getCols());
                floor.setStage(stage);
                floors.add(floor);
              });
      stage.setFloors(floors);
      stage.setVenue(venue);
      stages.add(stage);
    }
    return stages;
  }

  /**
   * @param venueId
   * @return
   */
  @Transactional
  public Optional<VenueDTO> getVenue(String venueId) {
    VenueDTO venueDTO = null;
    try {
      Venue venue = repository.getOne(venueId);
      venueDTO = mapEntityToDTO(venue);
    } catch (javax.persistence.EntityNotFoundException e) {
      LOGGER.error("Venue with id:{}   not found ", venueId, e);
    }
    return Optional.ofNullable(venueDTO);
  }

  /**
   * @param venue
   * @return
   */
  private VenueDTO mapEntityToDTO(Venue venue) {
    VenueDTO venueDTO = new VenueDTO();
    AddressDTO addressDTO = createAddressDTO(venue);
    venueDTO.setAddress(addressDTO);
    List<StageDTO> stagesDTO = new ArrayList<>();
    // this is O(n^2) however the n is small .  Max 10 stages and 2 floors or max 5 floors.
    for (Stage stage : venue.getStages()) {
      StageDTO stageDTO = new StageDTO();
      stageDTO.setName(stage.getName());
      stageDTO.setStageId(stage.getStageId());
      List<FloorDTO> floorsDTO = new ArrayList<>();
      for (Floor floor : stage.getFloors()) {
        FloorDTO floorDTO = createFloorDTO(floor);
        floorsDTO.add(floorDTO);
      }
      stageDTO.setFloors(floorsDTO);
      stagesDTO.add(stageDTO);
    }
    venueDTO.setStages(stagesDTO);
    venueDTO.setName(venue.getName());
    venueDTO.setVenueId(venue.getVenueId());
    return venueDTO;
  }

  /**
   * @param floor
   * @return
   */
  private FloorDTO createFloorDTO(Floor floor) {
    FloorDTO floorDTO = new FloorDTO();
    floorDTO.setCols(floor.getCols());
    floorDTO.setFloor(floor.getFloor());
    floorDTO.setFlorId(floor.getFlorId());
    floorDTO.setRows(floor.getRows());
    return floorDTO;
  }

  /**
   * @param venue
   * @return
   */
  private AddressDTO createAddressDTO(Venue venue) {
    AddressDTO addressDTO = new AddressDTO();
    addressDTO.setCity(venue.getAddress().getCity());
    addressDTO.setId(venue.getAddress().getId());
    addressDTO.setState(venue.getAddress().getState());
    addressDTO.setStreet(venue.getAddress().getStreet());
    addressDTO.setZipCode(venue.getAddress().getZipCode());
    return addressDTO;
  }

  /**
   * @param venue
   * @return
   */
  @Transactional
  public Optional<Venue> updateVenue(Venue venue) {
    Venue currentVenue = repository.getOne(venue.getVenueId());
    if (currentVenue == null) return Optional.empty();

    mapCurrentVenue(currentVenue, venue);
    repository.save(currentVenue);
    LOGGER.info("Updated Venue:{} with Id:{}", venue.getName(), venue.getVenueId());
    return Optional.ofNullable(venue);
  }

  @Transactional
  public Optional<String> deleteVenue(String venueId) {
    Venue currentVenue = repository.getOne(venueId);
    if (currentVenue == null) return Optional.empty();
    repository.deleteById(venueId);
    LOGGER.info("Deleted Venue with Id:{}", venueId);
    return Optional.of(venueId);
  }

  /**
   * Assign values of incoming venue to current venue before updates.
   *
   * @param currentVenue
   * @param venue
   */
  private void mapCurrentVenue(Venue currentVenue, Venue venue) {
    currentVenue.setName(venue.getName());
    currentVenue.setAddress(venue.getAddress());
    currentVenue.setStages(venue.getStages());
  }
}
