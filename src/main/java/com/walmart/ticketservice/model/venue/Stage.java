package com.walmart.ticketservice.model.venue;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(
  name = "stage",
  indexes = {@Index(name = "IDX_STAGE", columnList = "id,VENUE_ID")}
)
public class Stage implements Serializable {
  static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", insertable = false, updatable = false, nullable = false)
  private String stageId;

  @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Floor> floors = new ArrayList<>();

  @Column(name = "name")
  private String name;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "VENUE_ID")
  private Venue venue;

  public void setFloors(List<Floor> floors) {
    for (Floor floor : floors) {
      floor.setStage(this);
    }
    this.floors = floors;
  }
}
