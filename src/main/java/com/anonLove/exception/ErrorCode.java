package com.anonLove.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Authentication & Authorization (401, 403)
    INVALID_CREDENTIALS(401, "AUTH001", "Invalid email or password"),
    INVALID_TOKEN(401, "AUTH002", "Invalid or expired token"),
    UNAUTHORIZED(401, "AUTH003", "Authentication required"),
    FORBIDDEN(403, "AUTH004", "Access denied"),

    // Email Verification (400, 409)
    INVALID_EMAIL_DOMAIN(400, "EMAIL001", "Invalid university email domain"),
    EMAIL_ALREADY_EXISTS(409, "EMAIL002", "Email already exists"),
    INVALID_OTP(400, "EMAIL003", "Invalid or expired OTP code"),
    OTP_NOT_FOUND(400, "EMAIL004", "OTP not found. Please request verification again"),

    // User (404)
    USER_NOT_FOUND(404, "USER001", "User not found"),

    // Post (400, 403, 404)
    POST_NOT_FOUND(404, "POST001", "Post not found"),
    POST_NOT_ACCESSIBLE(403, "POST002", "This post is not accessible to you"),
    INVALID_VISIBILITY_TYPE(400, "POST003", "Invalid visibility type"),

    // Category (404)
    CATEGORY_NOT_FOUND(404, "CATEGORY001", "Category not found"),

    // Comment (403, 404)
    COMMENT_NOT_FOUND(404, "COMMENT001", "Comment not found"),
    NOT_COMMENT_AUTHOR(403, "COMMENT002", "Only comment author can modify"),

    // Chat (403, 404, 409)
    CHAT_ROOM_NOT_FOUND(404, "CHAT001", "Chat room not found"),
    NOT_POST_AUTHOR(403, "CHAT002", "Only post author can initiate chat"),
    NOT_CHAT_PARTICIPANT(403, "CHAT003", "You are not a participant of this chat"),
    CHAT_ROOM_ALREADY_EXISTS(409, "CHAT004", "Chat room already exists"),

    // Validation (400)
    INVALID_INPUT(400, "COMMON001", "Invalid input"),

    // Server Error (500)
    INTERNAL_SERVER_ERROR(500, "COMMON002", "Internal server error"),
    AI_SERVICE_ERROR(500, "COMMON003", "AI service temporarily unavailable");

    private final int status;
    private final String code;
    private final String message;
}
