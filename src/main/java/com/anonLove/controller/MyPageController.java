package com.anonLove.controller;

import com.anonLove.dto.response.MyPageCommentResponse;
import com.anonLove.dto.response.MyPagePostResponse;
import com.anonLove.security.CustomUserDetails;
import com.anonLove.service.mypage.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/posts")
    public ResponseEntity<List<MyPagePostResponse>> getMyPosts(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<MyPagePostResponse> response = myPageService.getMyPosts(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/comments")
    public ResponseEntity<List<MyPageCommentResponse>> getMyComments(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        List<MyPageCommentResponse> response = myPageService.getMyComments(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
