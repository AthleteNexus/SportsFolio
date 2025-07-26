package com.tech.service;

import com.tech.commons.exception.ResourceNotFoundException;
import com.tech.dto.CommentDTO;
import com.tech.entities.Comment;
import com.tech.entities.Post;
import com.tech.entities.Users;
import com.tech.repository.CommentRepository;
import com.tech.repository.PostRepository;
import com.tech.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UsersRepository userRepository;

    @Transactional
    public CommentDTO createComment(Long postId, String content, String username) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
        Users user = userRepository.findByName(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setPost(post);
        comment.setAuthor(user);

        Comment savedComment = commentRepository.save(comment);
        return new CommentDTO(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getPostComments(Long postId) {
        return commentRepository.findByPostIdOrderByCreatedAtDesc(postId)
                .stream()
                .map(CommentDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        if (!comment.getAuthor().getName().equals(username)) {
            throw new ResourceNotFoundException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
