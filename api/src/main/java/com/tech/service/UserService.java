package com.tech.service;

import com.tech.dto.UpdateUserRequest;
import com.tech.dto.UserDTO;
import com.tech.entities.Users;
import java.util.List;
import java.util.Optional;

public interface UserService {
    UserDTO getCurrentUser(String username);
    UserDTO getUserProfile(String username, String currentUsername);
    UserDTO updateProfile(String username, UpdateUserRequest request);
    List<UserDTO> getUserFollowers(String username, String currentUsername);
    List<UserDTO> getUserFollowing(String username, String currentUsername);
    void followUser(String followerUsername, String followedUsername);
    void unfollowUser(String followerUsername, String followedUsername);
    List<UserDTO> searchUsers(String query, int page, int size);

    Users findUserByUsername(String username);

    void updateRefreshToken(String username, String refreshToken);

    Optional<Users> findByUsername(String username);

    Optional<Users> findByEmailId(String emailId);
}
