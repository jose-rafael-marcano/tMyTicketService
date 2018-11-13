package com.walmart.ticketservice.model.venue;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

/** Assuming same cols per row. Class used for controler */
@Entity
@Setter
@Getter
@Table(name = "floor")
public class Floor implements Serializable {
  static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", insertable = false, updatable = false, nullable = false)
  private String florId;

  private int floor;
  private int cols;
  private int rows;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "STAGE_ID")
  private Stage stage;
}
