package com.tech.service;

import com.tech.commons.exception.ResourceNotFoundException;
import com.tech.entities.Post;
import com.tech.entities.PostLike;
import com.tech.entities.Users;
import com.tech.repository.PostLikeRepository;
import com.tech.repository.PostRepository;
import com.tech.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final PostLikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UsersRepository userRepository;

    @Transactional
    public void likePost(Long postId, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        Users user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!likeRepository.existsByPostIdAndUserId(postId, user.getId())) {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
        }
    }

    @Transactional
    public void unlikePost(Long postId, String username) {
        Users user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        likeRepository.findByPostIdAndUserId(postId, user.getId())
                .ifPresent(likeRepository::delete);
    }

    @Transactional(readOnly = true)
    public boolean hasUserLikedPost(Long postId, String username) {
        Users user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return likeRepository.existsByPostIdAndUserId(postId, user.getId());
    }

    @Transactional(readOnly = true)
    public long getPostLikesCount(Long postId) {
        return likeRepository.countByPostId(postId);
    }
}
