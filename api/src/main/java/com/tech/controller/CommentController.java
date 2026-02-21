package com.tech.controller;

import com.tech.dto.CommentDTO;
import com.tech.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(commentService.createComment(postId, content, userDetails.getUsername()));
    }

    @GetMapping
    public ResponseEntity<List<CommentDTO>> getPostComments(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getPostComments(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
