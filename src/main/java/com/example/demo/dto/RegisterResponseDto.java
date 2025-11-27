package com.example.demo.dto;

public class RegisterResponseDto {

    private boolean success;
    private String message;
    private String idToken;
    private String refreshToken;
    private String firebaseUid;

    public RegisterResponseDto() {}

    public RegisterResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RegisterResponseDto(
            boolean success,
            String message,
            String idToken,
            String refreshToken,
            String firebaseUid
    ) {
        this.success = success;
        this.message = message;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.firebaseUid = firebaseUid;
    }

    // getters & setters
}
