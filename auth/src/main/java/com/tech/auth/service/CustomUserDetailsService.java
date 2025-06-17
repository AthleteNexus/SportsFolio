package com.tech.auth.service;

import com.tech.dao.UsersDAO;
import com.tech.entities.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersDAO usersDAO;
    // check for custom user details service implementation and how it integrates with Spring Security
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersDAO.findByUserName(username);
        if(user != null) {
            return User.withUsername(username)
                    .password(user.getPasswordHash())
                    .authorities("ROLE_USER")
                    .build();
        } else
            throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
