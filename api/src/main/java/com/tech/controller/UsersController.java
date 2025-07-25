package com.tech.controller;

import com.tech.commons.exception.UserNotFoundException;
import com.tech.dto.UserDTO;
import com.tech.entities.Users;
import com.tech.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable(name = "username") String username) {
        logger.info("Fetching user by username: {}", username);
        Optional<Users> userOptional = userService.findByUsername(username);

        if (userOptional.isEmpty()) {
            logger.error("User not found with username: {}", username);
            throw new UserNotFoundException("User not found with username: " + username);
        }

        Users user = userOptional.get();
        UserDTO userDTO = new UserDTO(user);
        System.out.println("UserDTO: " + userDTO);
        return ResponseEntity.ok(userDTO);
    }


}

