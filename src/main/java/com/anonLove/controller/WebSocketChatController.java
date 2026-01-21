package com.anonLove.controller;

import com.anonLove.domain.chat.ChatMessage;
import com.anonLove.domain.chat.ChatRoom;
import com.anonLove.domain.chat.MessageType;
import com.anonLove.domain.user.User;
import com.anonLove.dto.request.chat.SendMessageRequest;
import com.anonLove.dto.response.chat.ChatMessageResponse;
import com.anonLove.exception.CustomException;
import com.anonLove.exception.ErrorCode;
import com.anonLove.repository.ChatMessageRepository;
import com.anonLove.repository.ChatRoomRepository;
import com.anonLove.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class WebSocketChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload SendMessageRequest request,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isParticipant(userId)) {
            throw new CustomException(ErrorCode.NOT_CHAT_PARTICIPANT);
        }

        User sender = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageType(MessageType.TEXT)
                .content(request.getContent())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(message);

        ChatMessageResponse response = ChatMessageResponse.from(savedMessage);

        // 채팅방 참여자들에게 메시지 전송
        messagingTemplate.convertAndSend("/queue/chat/" + roomId, response);

        log.info("Message sent: roomId={}, senderId={}", roomId, userId);
    }
}