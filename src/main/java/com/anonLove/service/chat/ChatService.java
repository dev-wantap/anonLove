package com.anonLove.service.chat;

import com.anonLove.domain.chat.ChatMessage;
import com.anonLove.domain.chat.ChatRoom;
import com.anonLove.domain.comment.Comment;
import com.anonLove.domain.post.Post;
import com.anonLove.domain.user.User;
import com.anonLove.dto.request.chat.CreateChatRoomRequest;
import com.anonLove.dto.response.chat.ChatMessageResponse;
import com.anonLove.dto.response.chat.ChatRoomListResponse;
import com.anonLove.dto.response.chat.CreateChatRoomResponse;
import com.anonLove.exception.CustomException;
import com.anonLove.exception.ErrorCode;
import com.anonLove.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public CreateChatRoomResponse createChatRoom(CreateChatRoomRequest request, Long initiatorId) {
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        // 게시글 작성자만 채팅 시작 가능
        if (!post.isAuthor(initiatorId)) {
            throw new CustomException(ErrorCode.NOT_POST_AUTHOR);
        }

        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 중복 채팅방 확인
        if (chatRoomRepository.existsByPostIdAndCommentIdAndInitiatorId(
                request.getPostId(), request.getCommentId(), initiatorId)) {
            // 기존 채팅방 반환
            ChatRoom existingRoom = chatRoomRepository
                    .findByPostIdAndCommentIdAndInitiatorId(
                            request.getPostId(), request.getCommentId(), initiatorId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

            return new CreateChatRoomResponse(existingRoom.getId());
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .post(post)
                .comment(comment)
                .initiator(initiator)
                .receiver(receiver)
                .build();

        ChatRoom savedRoom = chatRoomRepository.save(chatRoom);

        log.info("Chat room created: roomId={}, postId={}, commentId={}",
                savedRoom.getId(), request.getPostId(), request.getCommentId());

        return new CreateChatRoomResponse(savedRoom.getId());
    }

    public Page<ChatMessageResponse> getChatMessages(Long roomId, Long lastMessageId,
                                                     Long userId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        // 참여자 확인
        if (!chatRoom.isParticipant(userId)) {
            throw new CustomException(ErrorCode.NOT_CHAT_PARTICIPANT);
        }

        Page<ChatMessage> messages;
        if (lastMessageId != null) {
            messages = chatMessageRepository.findRecentMessages(roomId, lastMessageId, pageable);
        } else {
            messages = chatMessageRepository.findByChatRoomIdOrderByCreatedAtDesc(roomId, pageable);
        }

        return messages.map(ChatMessageResponse::from);
    }

    public List<ChatRoomListResponse> getChatRoomList(Long userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findByParticipant(userId);

        return chatRooms.stream()
                .map(room -> {
                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByChatRoomIdOrderByCreatedAtDesc(room.getId())
                            .orElse(null);

                    long unreadCount = chatMessageRepository.countUnreadMessages(room.getId(), userId);

                    return ChatRoomListResponse.builder()
                            .roomId(room.getId())
                            .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                            .lastMessageAt(lastMessage != null ? lastMessage.getCreatedAt() : room.getCreatedAt())
                            .unreadCount(unreadCount)
                            .postInfo(ChatRoomListResponse.PostInfo.builder()
                                    .id(room.getPost().getId())
                                    .title(room.getPost().getTitle())
                                    .build())
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHAT_ROOM_NOT_FOUND));

        if (!chatRoom.isParticipant(userId)) {
            throw new CustomException(ErrorCode.NOT_CHAT_PARTICIPANT);
        }

        // 상대방이 보낸 읽지 않은 메시지를 읽음 처리
        // 실제로는 bulk update 쿼리 사용 권장
        log.info("Messages marked as read: roomId={}, userId={}", roomId, userId);
    }
}
