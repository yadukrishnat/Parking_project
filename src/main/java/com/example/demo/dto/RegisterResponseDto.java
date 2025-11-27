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

    public RegisterResponseDto(boolean success, String message, String idToken, String refreshToken, String firebaseUid) {
        this.success = success;
        this.message = message;
        this.idToken = idToken;
        this.refreshToken = refreshToken;
        this.firebaseUid = firebaseUid;
    }

    // ✅ Public getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getFirebaseUid() { return firebaseUid; }
    public void setFirebaseUid(String firebaseUid) { this.firebaseUid = firebaseUid; }
}
