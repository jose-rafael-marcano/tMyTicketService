package com.walmart.ticketservice.model.venue;

import com.walmart.ticketservice.model.Address;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Setter
@Getter
@Entity
@Table(name = "venue")
public class Venue implements Serializable {
  static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id")
  private String venueId;

  private String name;

  @OneToMany(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Stage> stages = new ArrayList<>();

  @OneToOne(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
  private Address address;

  public void removeStage(Stage stage) {
    stages.remove(stage);
    stage.setVenue(null);
  }

  public void setStages(List<Stage> stages) {
    for (Stage stage : stages) {
      stage.setVenue(this);
    }
    this.stages = stages;
  }

  public void setAddress(Address address) {
    address.setVenue(this);
    this.address = address;
  }
}
