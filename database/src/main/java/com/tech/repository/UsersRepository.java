package com.tech.repository;

import com.tech.entities.Users;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {

    @Query("SELECT u FROM Users u WHERE u.name = ?1 OR u.email = ?2")
    List<Users> findByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM Users u WHERE u.name = ?1")
    Optional<Users> findByName(String username);
}
