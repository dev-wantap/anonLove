package com.anonLove.repository;

import com.anonLove.domain.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 중복 채팅방 확인
    boolean existsByPostIdAndCommentIdAndInitiatorId(Long postId, Long commentId, Long initiatorId);

    // 기존 채팅방 조회 (추가!)
    Optional<ChatRoom> findByPostIdAndCommentIdAndInitiatorId(Long postId, Long commentId, Long initiatorId);

    // 사용자가 참여 중인 채팅방 목록
    @Query("SELECT cr FROM ChatRoom cr " +
            "WHERE cr.initiator.id = :userId OR cr.receiver.id = :userId " +
            "ORDER BY cr.createdAt DESC")
    List<ChatRoom> findByParticipant(@Param("userId") Long userId);
}