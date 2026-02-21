package com.tech.controller;

import com.tech.dto.PostDTO;
import com.tech.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody String content,
                                            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(postService.createPost(content, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<PostDTO>> getNewsFeed() {
        return ResponseEntity.ok(postService.getNewsFeed());
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<List<PostDTO>> getUserPosts(@PathVariable String username) {
        return ResponseEntity.ok(postService.getUserPosts(username));
    }
}
