package com.example.demo.dto;

import java.util.List;



import lombok.Data;

@Data
public class LandRequest {
    private String place;
    private double latitude;
    private double longitude;
    private String availableDays;
    private String timeSlots;
    private int units;
    private Long userId;  // ✅ Add this
}

