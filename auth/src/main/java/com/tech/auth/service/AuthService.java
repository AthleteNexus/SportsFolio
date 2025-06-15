package com.tech.auth.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    // Dummy user store for demonstration. Replace with DB/user service in production.
    private static final Map<String, String> USER_STORE = new HashMap<>();
    static {
        USER_STORE.put("user", "password");
    }

    public boolean authenticate(String username, String password) {
        return USER_STORE.containsKey(username) && USER_STORE.get(username).equals(password);
    }
}

