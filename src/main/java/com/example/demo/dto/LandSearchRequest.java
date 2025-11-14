package com.example.demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandSearchRequest {
    private Double latitude;
    private Double longitude;
    private String date; // example: "2025-11-14"
}
