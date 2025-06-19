package com.tech.auth.service;

import com.tech.auth.dto.CustomUserDetails;
import com.tech.dao.UsersDAO;
import com.tech.entities.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UsersDAO usersDAO;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = usersDAO.findByUserName(username);
        if(user != null) {
            return new CustomUserDetails(user);
        } else
            throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
