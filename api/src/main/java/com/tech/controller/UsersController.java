package com.tech.controller;

import com.tech.commons.exception.UserNotFoundException;
import com.tech.entities.Users;
import com.tech.dao.UsersDAO;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UsersDAO usersDAO;

    public UsersController(UsersDAO usersDAO) {
        this.usersDAO = usersDAO;
    }

    @GetMapping("/{id}")
    public Users getUserById(@PathVariable Long id) {
        Optional<Users> user = usersDAO.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
        return user.get();
    }
    @GetMapping("/dummy")
    public String getDummyMessage() {
        return "Hello from UsersController!";
    }
}

