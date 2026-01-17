package com.anonLove.dto.response.comment;

import com.anonLove.domain.comment.Comment;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCommentResponse {
    private Long id;
    private boolean isFiltered;

    public static CreateCommentResponse from(Comment comment) {
        return CreateCommentResponse.builder()
                .id(comment.getId())
                .isFiltered(comment.isFiltered())
                .build();
    }
}
