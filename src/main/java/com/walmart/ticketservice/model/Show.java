package com.walmart.ticketservice.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@Entity
@Table(
  name = "shows",
  uniqueConstraints = {
    @UniqueConstraint(columnNames = {"venue", "stage", "presentation_time"}),
    @UniqueConstraint(columnNames = {"group_or_artist", "presentation_time"})
  }
)
public class Show implements Serializable {
  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", updatable = false, nullable = false)
  private String id;

  @NotNull private String name;

  @NotNull
  @Column(name = "group_or_artist")
  private String artist; // could be a band or group

  @NotNull
  @Column(name = "venue")
  private String venueId;

  @Column(name = "stage")
  @NotNull
  private String stageId;

  @Column(name = "presentation_time")
  // private Date time;
  @NotNull
  private LocalDateTime time;

  @Min(60)
  @Max(600)
  private int duration; // durantion in minutos//

  private BigDecimal priceTickets; // assuming same prize per seat.
  private ShowStatus Status;
  @Version private long version;
}
