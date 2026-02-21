package com.tech.service.impl;

import com.tech.commons.exception.ResourceNotFoundException;
import com.tech.commons.exception.UserNotFoundException;
import com.tech.dao.UsersDAO;
import com.tech.dto.UpdateUserRequest;
import com.tech.dto.UserDTO;
import com.tech.entities.Users;
import com.tech.repository.UsersRepository;
import com.tech.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    private final UsersRepository userRepository;
    private final UsersDAO usersDAO;

    @Override
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser(String username) {
        Users user = findUserByUsername(username);
        return new UserDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserProfile(String username, String currentUsername) {
        Users user = findUserByUsername(username);
        Users currentUser = findUserByUsername(currentUsername);
        UserDTO dto = new UserDTO(user);
        dto.setIsFollowing(user.getFollowers().contains(currentUser));
        return dto;
    }

    @Override
    @Transactional
    public UserDTO updateProfile(String username, UpdateUserRequest request) {
        Users user = findUserByUsername(username);

        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }
        if (request.getEmail() != null) {
            user.setEmail(request.getEmail());
        }
        if (request.getProfilePicture() != null) {
            user.setProfilePicture(request.getProfilePicture());
        }

        return new UserDTO(userRepository.save(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUserFollowers(String username, String currentUsername) {
        Users user = findUserByUsername(username);
        Users currentUser = findUserByUsername(currentUsername);
        return user.getFollowers().stream()
                .map(follower -> {
                    UserDTO dto = new UserDTO(follower);
                    dto.setIsFollowing(follower.getFollowers().contains(currentUser));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getUserFollowing(String username, String currentUsername) {
        Users user = findUserByUsername(username);
        Users currentUser = findUserByUsername(currentUsername);
        return user.getFollowing().stream()
                .map(following -> {
                    UserDTO dto = new UserDTO(following);
                    dto.setIsFollowing(following.getFollowers().contains(currentUser));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void followUser(String followerUsername, String followedUsername) {
        if (followerUsername.equals(followedUsername)) {
            throw new IllegalArgumentException("Users cannot follow themselves");
        }

        Users follower = findUserByUsername(followerUsername);
        Users followed = findUserByUsername(followedUsername);

        if (!follower.getFollowing().contains(followed)) {
            follower.getFollowing().add(followed);
            followed.getFollowers().add(follower);
            userRepository.save(follower);
            userRepository.save(followed);
        }
    }

    @Override
    @Transactional
    public void unfollowUser(String followerUsername, String followedUsername) {
        Users follower = findUserByUsername(followerUsername);
        Users followed = findUserByUsername(followedUsername);

        follower.getFollowing().remove(followed);
        followed.getFollowers().remove(follower);
        userRepository.save(follower);
        userRepository.save(followed);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> searchUsers(String query, int page, int size) {
       return null; // Implement search logic here
        // Example: return userRepository.searchByUsernameOrBio(query, PageRequest.of(page, size))
        //         .stream().map(UserDTO::new).collect(Collectors.toList());
        // Note: This method should be implemented based on your search requirements.
    }

    @Override
    public Users findUserByUsername(String username) {
        return userRepository.findByName(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
    }

    @Override
    public void updateRefreshToken(String username, String refreshToken) {
        logger.info("Updating refresh token for user: {}", username);
        Optional<Users> userOptional = usersDAO.findByUserName(username);
        if (userOptional.isPresent()) {
            Users user = userOptional.get();
            user.setRefreshToken(refreshToken);
            usersDAO.saveUser(user);
            logger.info("Refresh token updated successfully for user: {}", username);
        } else {
            logger.warn("User not found for username: {}", username);
            throw new UserNotFoundException("User not found with username: " + username);
        }
    }

    @Override
    public Optional<Users> findByUsername(String username) {
        return usersDAO.findByUserName(username);
    }
}
