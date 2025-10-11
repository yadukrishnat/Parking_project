package com.example.demo.dto;

import java.util.List;



import lombok.Data;

@Data
public class LandRequest {
    private String place;
    private Double latitude;
    private Double longitude;
    private List<String> availableDays;
    private List<String> timeSlots;
    private int units;
    // getters and setters
}

