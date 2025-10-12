package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LandListResponse {
    private Long id;
    private String place;
    private Double latitude;
    private Double longitude;
    private String availableDays;
    private String timeSlots;
    private int units;
}

