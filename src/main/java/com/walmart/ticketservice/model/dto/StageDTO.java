package com.walmart.ticketservice.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class StageDTO implements Serializable {
    static final long serialVersionUID = 1L;
    private String stageId;
    private List<FloorDTO> floors = new ArrayList<>();
    private String name;


}
