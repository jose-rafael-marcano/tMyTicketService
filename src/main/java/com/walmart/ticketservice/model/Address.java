package com.walmart.ticketservice.model;

import com.walmart.ticketservice.model.venue.Venue;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

@Setter
@Getter
@Entity
@Table(name = "address")
public class Address implements Serializable {
  static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", insertable = false, updatable = false, nullable = false)
  private String id;

  private String street;
  private String state;
  private String city;
  private String zipCode;

  @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JoinColumn(name = "venue_id")
  private Venue venue;
}
