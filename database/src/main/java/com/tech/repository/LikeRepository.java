package com.tech.repository;

import com.tech.entities.Like;
import com.tech.entities.Post;
import com.tech.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostAndUser(Post post, Users user);
    void deleteByPostAndUser(Post post, Users user);
}
