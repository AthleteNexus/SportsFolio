package com.tech.dto;

import com.tech.entities.Post;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PostDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private LocalDateTime createdAt;
    private long likesCount;
    private long commentsCount;

    public PostDTO(Post post) {
        this.id = post.getId();
        this.content = post.getContent();
        this.authorUsername = post.getAuthor().getName();
        this.createdAt = post.getCreatedAt();
        this.likesCount = post.getLikes().size();
        this.commentsCount = post.getComments().size();
    }
}
