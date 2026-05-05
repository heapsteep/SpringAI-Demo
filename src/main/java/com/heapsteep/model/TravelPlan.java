package com.heapsteep.model;

import lombok.Data;

import java.util.List;

@Data
public class TravelPlan {
    private String city;
    private Integer days;
    private List<DayPlan> itinerary;
}
