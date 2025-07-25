package com.tech.dto;

import com.tech.entities.Users;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDTO {
    
    private String username;
    private String bio;
    private Boolean isTrainer;

    public UserDTO(Users users) {
        this.username = users.getName();
        this.bio = users.getBio();
        this.isTrainer = users.getIsTrainer();
    }

}
