package com.tech.dto;

import com.tech.entities.Users;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class UserDTO {
    private String username;
    private String bio;
    private Boolean isTrainer;
    private String email;
    private String profilePicture;
    private LocalDateTime createdAt;
    private Integer followersCount;
    private Integer followingCount;
    private Boolean isFollowing; // To indicate if the requesting user follows this user

    public UserDTO(Users users) {
        this.username = users.getName();
        this.bio = users.getBio();
        this.isTrainer = users.getIsTrainer();
        this.email = users.getEmail();
        this.profilePicture = users.getProfilePicture();
        this.createdAt = users.getCreatedAt();
        this.followersCount = users.getFollowers() != null ? users.getFollowers().size() : 0;
        this.followingCount = users.getFollowing() != null ? users.getFollowing().size() : 0;
    }
}
