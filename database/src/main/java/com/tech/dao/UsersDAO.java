package com.tech.dao;

import com.tech.entities.Users;
import com.tech.repository.UsersRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UsersDAO {

    private final UsersRepository usersRepository;

    public UsersDAO(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void saveUser(Users user) {
        usersRepository.save(user);
    }

    public void deleteUser(Users user) {
        usersRepository.delete(user);
    }

    public List<Users> findAll() {
        return usersRepository.findAll();
    }

    public Optional<Users> findById(Long id) {
        return usersRepository.findById(id);
    }

    public Boolean checkIfUserExists(String username, String email) {
        List<Users> users = usersRepository.findByUsernameOrEmail(username, email);
        return !users.isEmpty();
    }

    public Optional<Users> findByUserName(String username) {
        return usersRepository.findByName(username);
    }
}
