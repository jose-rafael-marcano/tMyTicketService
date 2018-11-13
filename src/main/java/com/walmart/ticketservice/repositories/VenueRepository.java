package com.walmart.ticketservice.repositories;

import com.walmart.ticketservice.model.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue,String> {

}
