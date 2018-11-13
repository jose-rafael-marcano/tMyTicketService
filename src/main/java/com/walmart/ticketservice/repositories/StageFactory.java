package com.walmart.ticketservice.repositories;

import com.walmart.ticketservice.model.venue.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StageFactory extends JpaRepository<Stage, String> {
  @Query("SELECT s FROM Stage s WHERE s.id =:stageId and VENUE_ID=:venueId")
  public Stage findByStageIdAndAndVenue(
      @Param("stageId") String stageId, @Param("venueId") String venueId);
}
