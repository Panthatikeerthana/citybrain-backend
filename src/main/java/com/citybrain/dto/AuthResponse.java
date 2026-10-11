package com.citybrain.dto;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private Long expiresIn;
    private UserDto user;

    public AuthResponse() {
    }

    public AuthResponse(String token, String type, Long expiresIn, UserDto user) {
        this.token = token;
        this.type = (type != null && !type.isBlank()) ? type : "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }
}
