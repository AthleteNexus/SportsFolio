package com.tech.auth.service;

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
    // user repository or user service can be injected here to fetch user details from a database or other source.
    // Dummy user store for demonstration. Replace with DB/user service in production.
    private static final Map<String, String> USER_STORE = new HashMap<>();
    static {
        USER_STORE.put("user", new BCryptPasswordEncoder().encode("password"));
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(USER_STORE.containsKey(username))
            return User.withUsername(username)
                .password(USER_STORE.get(username))
                .authorities("ROLE_USER")
                .build();
        else
            throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
