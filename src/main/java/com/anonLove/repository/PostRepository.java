package com.anonLove.repository;

import com.anonLove.domain.post.Post;
import com.anonLove.domain.post.TargetGender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    // 카테고리별 조회
    Page<Post> findByCategoryId(Integer categoryId, Pageable pageable);

    // 사용자별 게시글 조회 (마이페이지용)
    List<Post> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 공개범위 필터링 쿼리
    @Query("SELECT p FROM Post p " +
            "WHERE (:categoryId IS NULL OR p.category.id = :categoryId) " +
            "AND (p.visibilityType = 'ALL' OR " +
            "     (p.visibilityType = 'HIDE_SAME_UNI' AND p.user.university != :university)) " +
            "AND (p.targetGender = 'ALL' OR p.targetGender = :gender)")
    Page<Post> findVisiblePosts(@Param("categoryId") Integer categoryId,
                                @Param("university") String university,
                                @Param("gender") TargetGender gender,
                                Pageable pageable);
}
