package com.anonLove.dto.response;

import com.anonLove.domain.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyPagePostResponse {
    private Long id;
    private String title;
    private String category;
    private int commentCount;
    private LocalDateTime createdAt;

    public static MyPagePostResponse from(Post post, int commentCount) {
        return MyPagePostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .category(post.getCategory() != null ? post.getCategory().getName() : null)
                .commentCount(commentCount)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
