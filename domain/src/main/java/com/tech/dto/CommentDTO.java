package com.tech.dto;

import com.tech.entities.Comment;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;

    public CommentDTO(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.authorUsername = comment.getAuthor().getName();
        this.createdAt = comment.getCreatedAt();
    }
}
