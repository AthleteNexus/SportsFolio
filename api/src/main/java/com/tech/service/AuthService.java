package com.tech.service;

import com.tech.auth.dto.AuthRequest;
import com.tech.commons.exception.InvalidEmailIdException;
import com.tech.commons.exception.InvalidPasswordException;
import com.tech.commons.exception.InvalidUsernameException;
import com.tech.dao.UsersDAO;
import com.tech.entities.Users;
import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);
    @Autowired
    private UsersDAO usersDAO;

    public void signup(AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmailId();
        if (username == null || username.isEmpty()) {
            throw new InvalidUsernameException("Username cannot be empty");
        }
        if (password == null || password.isEmpty()) {
            throw new InvalidPasswordException("Password cannot be empty");
        }
        if (email == null || email.isEmpty()) {
            throw new InvalidEmailIdException("EmailId cannot be empty");
        }
        Boolean usersAlreadyExist = usersDAO.checkIfUserExists(username, email);
        if( usersAlreadyExist ) {
            throw new InvalidUsernameException("User with this username or email already exists");
        }

        // Create a new user entity and save it
        Users user = new Users();
        user.setName(username);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setEmailId(email);
        user.setUserRole("ROLE_USER");
        usersDAO.saveUser(user);
    }
}
