package com.anonLove.controller;

import com.anonLove.dto.request.chat.CreateChatRoomRequest;
import com.anonLove.dto.response.chat.ChatMessageResponse;
import com.anonLove.dto.response.chat.ChatRoomListResponse;
import com.anonLove.dto.response.chat.CreateChatRoomResponse;
import com.anonLove.security.CustomUserDetails;
import com.anonLove.service.chat.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/rooms")
    public ResponseEntity<CreateChatRoomResponse> createChatRoom(
            @Valid @RequestBody CreateChatRoomRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        CreateChatRoomResponse response = chatService.createChatRoom(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<ChatRoomListResponse> response = chatService.getChatRoomList(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/rooms/{roomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> getChatMessages(
            @PathVariable Long roomId,
            @RequestParam(required = false) Long lastMessageId,
            @PageableDefault(size = 50, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Page<ChatMessageResponse> response = chatService.getChatMessages(
                roomId, lastMessageId, userDetails.getUserId(), pageable);
        return ResponseEntity.ok(response);
    }
}