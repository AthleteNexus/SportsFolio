package com.tech.dto;

public class AuthResponse {
    private final String accessToken;
    private final String refreshToken;
    private final UserDTO userDTO;

    public AuthResponse(String accessToken, String refreshToken, UserDTO userDTO) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userDTO = userDTO;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public UserDTO getUserDTO() {
        return userDTO;
    }

}
