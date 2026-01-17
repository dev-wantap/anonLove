package com.anonLove.dto.request.chat;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateChatRoomRequest {

    @NotNull(message = "Post ID is required")
    private Long postId;

    @NotNull(message = "Comment ID is required")
    private Long commentId;

    @NotNull(message = "Receiver ID is required")
    private Long receiverId;
}
