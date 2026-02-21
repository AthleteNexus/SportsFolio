package com.tech.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username"),
                @UniqueConstraint(columnNames = "email")
        }
)
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSports> userSports;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "email_id", unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "bio", length = 255)
    private String bio;

    @Column(name = "profile_picture_url")
    private String profilePicture;

    @Column(name = "is_trainer")
    private Boolean isTrainer;

    @ManyToMany
    @JoinTable(
        name = "user_followers",
        joinColumns = @JoinColumn(name = "followed_id"),
        inverseJoinColumns = @JoinColumn(name = "follower_id")
    )
    private Set<Users> followers = new HashSet<>();

    @ManyToMany(mappedBy = "followers")
    private Set<Users> following = new HashSet<>();

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "user_role")
    private String userRole;

    @Column(name = "refresh_token")
    private String refreshToken;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addFollower(Users follower) {
        followers.add(follower);
        follower.getFollowing().add(this);
    }

    public void removeFollower(Users follower) {
        followers.remove(follower);
        follower.getFollowing().remove(this);
    }
}
