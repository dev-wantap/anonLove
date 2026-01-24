package com.anonLove.dto.response.auth;

import com.anonLove.domain.user.Gender;
import com.anonLove.domain.user.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    private String nickname;
    private String university;
    private Gender gender;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .university(user.getUniversity())
                .gender(user.getGender())
                .build();
    }
}
