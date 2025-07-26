package com.tech.controller;

import com.tech.dto.UpdateUserRequest;
import com.tech.dto.UserDTO;
import com.tech.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    private final Logger logger = LoggerFactory.getLogger(UsersController.class);
    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getCurrentUser(userDetails.getUsername()));
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserProfile(username, userDetails.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateProfile(
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.updateProfile(userDetails.getUsername(), request));
    }

    @GetMapping("/{username}/followers")
    public ResponseEntity<List<UserDTO>> getUserFollowers(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserFollowers(username, userDetails.getUsername()));
    }

    @GetMapping("/{username}/following")
    public ResponseEntity<List<UserDTO>> getUserFollowing(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getUserFollowing(username, userDetails.getUsername()));
    }

    @PostMapping("/follow/{username}")
    public ResponseEntity<Void> followUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.followUser(userDetails.getUsername(), username);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/unfollow/{username}")
    public ResponseEntity<Void> unfollowUser(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {
        userService.unfollowUser(userDetails.getUsername(), username);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.searchUsers(query, page, size));
    }
}
