package com.tech.service;

import com.tech.auth.dto.AuthRequest;
import com.tech.commons.enums.UserRole;
import com.tech.commons.exception.InvalidEmailIdException;
import com.tech.commons.exception.InvalidPasswordException;
import com.tech.commons.exception.InvalidUsernameException;
import com.tech.commons.exception.UserAlreadyExistsException;
import com.tech.commons.util.EmailValidator;
import com.tech.commons.util.PasswordValidator;
import com.tech.commons.util.UsernameValidator;
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

    private final UsersDAO usersDAO;
    private final UsernameValidator usernameValidator;
    private final PasswordValidator passwordValidator;
    private final EmailValidator emailValidator;

    public AuthService(
            UsersDAO usersDAO, UsernameValidator usernameValidator, PasswordValidator passwordValidator,
            EmailValidator emailValidator
    ) {
        this.usersDAO = usersDAO;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.emailValidator = emailValidator;
    }


    public void signup(AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String email = request.getEmailId();
        logger.info("Signing up user: {}", username);
        // Check if user already exists
        Boolean usersAlreadyExist = usersDAO.checkIfUserExists(username, email);
        if( usersAlreadyExist ) {
            throw new UserAlreadyExistsException("User with this username or email already exists");
        }
        // Validate username
        usernameValidator.validate(username);
        // Validate password
        passwordValidator.validate(password);
        // Validate email
        emailValidator.validate(email);

        // Create a new user entity and save it
        Users user = new Users();
        user.setName(username);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setEmailId(email);
        user.setUserRole("ROLE_"+ UserRole.USER);
        usersDAO.saveUser(user);
    }
}
