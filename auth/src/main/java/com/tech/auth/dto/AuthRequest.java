package com.tech.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class AuthRequest {
    private String username;
    private String password;
    private String emailId;

    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthRequest(String username, String password, String emailId) {
        this.username = username;
        this.password = password;
        this.emailId = emailId;
    }
}
