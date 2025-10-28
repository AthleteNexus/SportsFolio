package com.tech.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class UpdateUserRequest {
    @Size(min = 3, max = 255, message = "Bio must be between 3 and 255 characters")
    private String bio;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String profilePicture;
}
