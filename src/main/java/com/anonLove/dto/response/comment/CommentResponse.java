package com.anonLove.dto.response.comment;

import com.anonLove.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {
    private Long id;
    private Long userId;
    private String content;
    private boolean isFiltered;
    private boolean isMine;
    private LocalDateTime createdAt;

    public static CommentResponse from(Comment comment, Long viewerId) {
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUser().getId())
                .content(comment.getContent())
                .isFiltered(comment.isFiltered())
                .isMine(comment.getUser().getId().equals(viewerId))
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
