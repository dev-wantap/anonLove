package com.anonLove.dto.response.chat;

import com.anonLove.domain.chat.ChatMessage;
import com.anonLove.domain.chat.MessageType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatMessageResponse {
    private Long id;
    private Long senderId;
    private MessageType messageType;
    private String content;
    private String fileUrl;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static ChatMessageResponse from(ChatMessage message) {
        return ChatMessageResponse.builder()
                .id(message.getId())
                .senderId(message.getSender().getId())
                .messageType(message.getMessageType())
                .content(message.getContent())
                .fileUrl(message.getFileUrl())
                .isRead(message.isRead())
                .createdAt(message.getCreatedAt())
                .build();
    }
}
