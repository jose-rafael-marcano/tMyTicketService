package com.walmart.ticketservice.repositories;

import com.walmart.ticketservice.model.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ShowRepository extends JpaRepository<Show, String> {
  @Query("SELECT s FROM Show s WHERE s.id =:showId")
  public List<Show> findByShowId(@Param("showId") String showId);
}
