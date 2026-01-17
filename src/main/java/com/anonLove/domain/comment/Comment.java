package com.anonLove.domain.comment;

import com.anonLove.domain.common.BaseTimeEntity;
import com.anonLove.domain.post.Post;
import com.anonLove.domain.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "is_filtered")
    private boolean isFiltered;

    @Builder
    public Comment(Post post, User user, String content, boolean isFiltered) {
        this.post = post;
        this.user = user;
        this.content = content;
        this.isFiltered = isFiltered;
    }

    public void update(String content, boolean isFiltered) {
        this.content = content;
        this.isFiltered = isFiltered;
    }

    public boolean isAuthor(Long userId) {
        return this.user.getId().equals(userId);
    }
}
