package com.tech.service;

import com.tech.commons.exception.ResourceNotFoundException;
import com.tech.dto.PostDTO;
import com.tech.entities.Post;
import com.tech.entities.Users;
import com.tech.repository.PostRepository;
import com.tech.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UsersRepository userRepository;

    @Transactional
    public PostDTO createPost(String content, String username) {
        Users user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Post post = new Post();
        post.setContent(content);
        post.setAuthor(user);

        Post savedPost = postRepository.save(post);
        return new PostDTO(savedPost);
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getNewsFeed() {
        return postRepository.findAllPostsForNewsfeed()
                .stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PostDTO getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        return new PostDTO(post);
    }

    @Transactional(readOnly = true)
    public List<PostDTO> getUserPosts(String username) {
        return postRepository.findByAuthorNameOrderByCreatedAtDesc(username)
                .stream()
                .map(PostDTO::new)
                .collect(Collectors.toList());
    }
}
