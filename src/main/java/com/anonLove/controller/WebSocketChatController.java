package com.anonLove.controller;

import com.anonLove.dto.request.chat.SendMessageRequest;
import com.anonLove.dto.response.chat.ChatMessageResponse;
import com.anonLove.repository.UserRepository;
import com.anonLove.service.chat.ChatService;
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
    private final UserRepository userRepository;
    private ChatService chatService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(
            @DestinationVariable Long roomId,
            @Payload SendMessageRequest request,
            Principal principal) {

        Long userId = Long.parseLong(principal.getName());

        // DB 작업 및 비즈니스 로직은 Service에서 처리 (Transactional 보장)
        ChatMessageResponse response = chatService.saveMessage(roomId, userId, request);

        // 채팅방 참여자들에게 메시지 전송
        messagingTemplate.convertAndSend("/queue/chat/" + roomId, response);

        log.info("Message sent and broadcasted: roomId={}, senderId={}", roomId, userId);
    }
}