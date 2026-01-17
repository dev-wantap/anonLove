package com.anonLove.repository;

import com.anonLove.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    long countByPostId(Long postId);

    // 게시글별 댓글 조회
    List<Comment> findByPostIdOrderByCreatedAtAsc(Long postId);

    // 사용자별 댓글 조회 (마이페이지용, Post 정보 fetch join)
    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByUserIdWithPost(@Param("userId") Long userId);
}