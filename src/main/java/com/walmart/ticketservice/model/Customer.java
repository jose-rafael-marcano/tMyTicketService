package com.walmart.ticketservice.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Setter
@Getter
@Entity
public class Customer implements Serializable {
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(generator = "UUID")
  @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
  @Column(name = "id", insertable = false, updatable = false, nullable = false)
  private String id;

  private String name;
  private int lastFourOfCard;
  private Date dateOfBirth;
  private String email;

  @OneToOne(mappedBy = "venue", cascade = CascadeType.ALL, orphanRemoval = true)
  private Address address;
}
