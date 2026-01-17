package com.anonLove.dto.response.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatRoomListResponse {
    private Long roomId;
    private String lastMessage;
    private LocalDateTime lastMessageAt;
    private long unreadCount;
    private PostInfo postInfo;

    @Getter
    @Builder
    public static class PostInfo {
        private Long id;
        private String title;
    }
}
