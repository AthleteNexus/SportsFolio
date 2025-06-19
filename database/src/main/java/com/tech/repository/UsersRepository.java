package com.tech.repository;

import com.tech.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.name = ?1 OR u.emailId = ?2")
    List<Users> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM Users u WHERE u.name = ?1")
    Users findByName(String username);
}
