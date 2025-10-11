package com.example.demo.dto;

public class LandListResponse {
    private Long id;
    private String place;
    private Double latitude;
    private Double longitude;
    private String availableDays;
    private String timeSlots;
    private int units;

    // Constructor
    public LandListResponse(Long id, String place, Double latitude, Double longitude,
                            String availableDays, String timeSlots, int units) {
        this.id = id;
        this.place = place;
        this.latitude = latitude;
        this.longitude = longitude;
        this.availableDays = availableDays;
        this.timeSlots = timeSlots;
        this.units = units;
    }

    // Getters and setters
    // ...
}
