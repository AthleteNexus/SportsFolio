package com.tech.service;

import com.tech.commons.exception.UserNotFoundException;
import com.tech.dao.UsersDAO;
import com.tech.entities.Users;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UsersDAO usersDAO;
    public UserService(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }
    public Optional<Users> findByUsername(String username) {
        return usersDAO.findByUserName(username);
    }

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
}
